package ar.fgsoruco.opencv4.factory.photo

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.FileInputStream
import java.io.InputStream
import io.flutter.plugin.common.MethodChannel
import org.opencv.photo.Photo

class DetailEnhanceFactory {
    companion object {

        fun process(
            pathType: Int,
            pathString: String,
            data: ByteArray,
            sigmaS: Float?,
            sigmaR: Float?,
            result: MethodChannel.Result
        ) {
            when (pathType) {
                1 -> result.success(detailEnhance(pathString, sigmaS, sigmaR))
                2 -> result.success(detailEnhance(data, sigmaS, sigmaR))
                3 -> result.success(detailEnhance(data, sigmaS, sigmaR))
            }
        }

        //Module: Computational Photography
        /**
         * @param sigmaS sigma spatial
         * @param sigmaR sigma range
         * @see <a href="https://learnopencv.com/non-photorealistic-rendering-using-opencv-python-c/">https://learnopencv.com/non-photorealistic-rendering-using-opencv-python-c</a>
         */
        private fun detailEnhance(pathData: String, sigmaS: Float?, sigmaR: Float?): ByteArray {
            val inputStream: InputStream = FileInputStream(pathData.replace("file://", ""))
            val data: ByteArray = inputStream.readBytes()

            return try {
                // Decode image from input byte array
                val filename = pathData.replace("file://", "")
                val src = Imgcodecs.imread(filename)
                execute(src, sigmaS, sigmaR)
            } catch (e: java.lang.Exception) {
                println("OpenCV Error: $e")
                data
            }
        }

        private fun detailEnhance(data: ByteArray, sigmaS: Float?, sigmaR: Float?): ByteArray {
            return try {
                // Decode image from input byte array
                val src = Imgcodecs.imdecode(MatOfByte(*data), Imgcodecs.IMREAD_UNCHANGED)
                execute(src, sigmaS, sigmaR)
            } catch (e: java.lang.Exception) {
                println("OpenCV Error: $e")
                data
            }
        }

        private fun execute(src: Mat, sigmaS: Float?, sigmaR: Float?): ByteArray {
            // Enhance Detail
            val dst = Mat()
            when {
                sigmaS == null -> Photo.detailEnhance(src, dst)
                sigmaR == null -> Photo.detailEnhance(src, dst, sigmaS)
                else -> Photo.detailEnhance(src, dst, sigmaS, sigmaR)
            }
            // instantiating an empty MatOfByte class
            val matOfByte = MatOfByte()
            // Converting the Mat object to MatOfByte
            Imgcodecs.imencode(".jpg", dst, matOfByte)
            return matOfByte.toArray()
        }

    }
}