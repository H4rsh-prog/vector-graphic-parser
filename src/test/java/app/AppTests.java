package app;

import org.junit.jupiter.api.Test;

public class AppTests {
    @Test 
    public void startTest(){
        String PATH = javax.swing.JOptionPane.showInputDialog("Enter Image Path : ");
        if(PATH.startsWith("\"")) PATH = PATH.substring(1);
        if(PATH.endsWith("\"")) PATH = PATH.substring(0, PATH.length()-1);
        // float BLUR_SIGMA = Float.valueOf(JOptionPane.showInputDialog("BLUR_SIGMA : "));
        // float MASK_THRESHOLD = Float.valueOf(JOptionPane.showInputDialog("MASK_THRESHOLD : "));
        // float SIMPLIFICATION_EPSILON = Float.valueOf(JOptionPane.showInputDialog("SIMPLIFICATION_EPSILON : "));
        // float CURVE_TOLERANCE = Float.valueOf(JOptionPane.showInputDialog("CURVE_TOLERANCE : "));
        float BLUR_SIGMA = .7f;
        float MASK_THRESHOLD_WEIGHT = -.3f;
        float SIMPLIFICATION_EPSILON = .2f;
        float CURVE_TOLERANCE = 1.5f;
        App.start(PATH, BLUR_SIGMA, MASK_THRESHOLD_WEIGHT, SIMPLIFICATION_EPSILON, CURVE_TOLERANCE);
    }

}
