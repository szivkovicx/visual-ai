import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class DataSetGenerator {

    public static DataSetIterator createDataSetIterator(List<List<INDArray>> features, List<String> labels, Map<String, Integer> labelMap) {
        List<DataSet> dataSets = new ArrayList<>();

        for (int i = 0; i < features.size(); i++) {
            String label = labels.get(i);
            Integer labelIndex = labelMap.get(label);

            if (labelIndex == null) {
                throw new IllegalArgumentException("Label index not found for label: " + label);
            }

            List<INDArray> featureTensors = features.get(i);
            for (INDArray featureTensor : featureTensors) {
                INDArray labelTensor = Nd4j.zeros(1, labelMap.size());
                labelTensor.putScalar(labelIndex, 1.0); // One-hot encoding

                DataSet dataSet = new DataSet(featureTensor, labelTensor);
                dataSets.add(dataSet);
            }
        }

        return new ListDataSetIterator<>(dataSets);
    }

    public static Map<String, Integer> createLabelMap(List<String> labels) {
        Map<String, Integer> labelMap = new HashMap<>();
        int index = 0;
        for (String label : labels) {
            if (!labelMap.containsKey(label)) {
                labelMap.put(label, index++);
            }
        }
        return labelMap;
    }
}