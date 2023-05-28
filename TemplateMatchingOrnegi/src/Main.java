import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static class ImageData {
        Mat image;
        String name;

        ImageData(Mat image, String name) {
            this.image = image;
            this.name = name;
        }
    }

    public static List<ImageData> readImages(String directory) {
        List<ImageData> images = new ArrayList<>();
        for (int i = 21; i < 2150; i++) {
            String imageName = i + ".png";
            String imagePath = directory +"/"+ imageName;
            if (!Files.exists(Paths.get(imagePath))) {
                continue;  // image file does not exist, skip this iteration
            }
            Mat img = Imgcodecs.imread(imagePath);
            images.add(new ImageData(img, imageName));
        }
        return images;
    }

    public static double compareImages(Mat img1, Mat img2) {
        Mat result = new Mat();
        Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_CCOEFF_NORMED);
        double maxVal = -1;
        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.cols(); j++) {
                double[] val = result.get(i, j);
                if (val[0] > maxVal) {
                    maxVal = val[0];
                }
            }
        }
        return maxVal;
    }

    public static Map<String, String> findMostSimilar(List<ImageData> images) {
        Map<String, String> mostSimilar = new HashMap<>();
        for (int i = 0; i < images.size(); i++) {
            double maxSimilarity = -1.0;
            int maxIndex = -1;
            for (int j = 0; j < images.size(); j++) {
                if (i == j) continue;
                double similarity = compareImages(images.get(i).image, images.get(j).image);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    maxIndex = j;
                }
            }
            mostSimilar.put(images.get(i).name, images.get(maxIndex).name);
        }
        return mostSimilar;
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String directory = "C:\\Users\\Ali Arslan\\Desktop\\image_template_mathing\\ALGORTIMA_ANALIZI_ODEV_SOURCES\\"; // Görüntülerin olduğu klasör
        List<ImageData> images = readImages(directory);
        Map<String, String> mostSimilar = findMostSimilar(images);
        for (Map.Entry<String, String> entry : mostSimilar.entrySet()) {
            System.out.println("Image " + entry.getKey() + " is most similar to image " + entry.getValue());
        }
    }
}