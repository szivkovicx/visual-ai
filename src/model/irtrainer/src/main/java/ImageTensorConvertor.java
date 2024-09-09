import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageTensorConvertor {
    // Initiate OpenCV native library
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Load image
    public static Mat loadImage(String filePath) {
        Mat image = Imgcodecs.imread(filePath);
        // Convert image to RGB if it's not already
        if (image.channels() == 1) {
            Mat rgbImage = new Mat();
            Imgproc.cvtColor(image, rgbImage, Imgproc.COLOR_GRAY2RGB);
            image.release(); // Release original image to free memory
            return rgbImage;
        }
        return image;
    }

    // Resize image to corresponding dimensions
    public static Mat resizeImage(Mat image, int width, int height) {
        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, new Size(width, height));
        return resizedImage;
    }

    // Convert image to INDArray
    public static INDArray imageToINDArray(Mat image) {
        // Ensure image is in RGB format
        if (image.channels() == 1) {
            Mat rgbImage = new Mat();
            Imgproc.cvtColor(image, rgbImage, Imgproc.COLOR_GRAY2RGB);
            image.release(); // Release original image to free memory
            image = rgbImage;
        }

        // Convert Mat to INDArray
        INDArray ndArray = Nd4j.create(image.rows(), image.cols(), image.channels());
        for (int row = 0; row < image.rows(); row++) {
            for (int col = 0; col < image.cols(); col++) {
                double[] pixel = image.get(row, col);
                for (int channel = 0; channel < image.channels(); channel++) {
                    // Normalize pixel values to [0, 1]
                    ndArray.putScalar(new int[]{row, col, channel}, pixel[channel] / 255.0);
                }
            }
        }
        // Transpose to the format [1, channels, height, width]
        return ndArray.permute(2, 0, 1).reshape(1, image.channels(), image.rows(), image.cols());
    }
}