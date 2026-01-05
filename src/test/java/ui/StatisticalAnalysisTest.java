package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for StatisticalAnalysis utility class.
 * Tests all statistical methods including edge cases and mathematical correctness.
 */
class StatisticalAnalysisTest {

    // Test calculateMean
    @Test
    void testCalculateMean() {
        // Test normal case
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        assertEquals(3.0, StatisticalAnalysis.calculateMean(values1), 0.01);
        
        // Test with negative values
        double[] values2 = {-5.0, -3.0, 0.0, 3.0, 5.0};
        assertEquals(0.0, StatisticalAnalysis.calculateMean(values2), 0.01);
        
        // Test with decimals
        double[] values3 = {1.5, 2.5, 3.5};
        assertEquals(2.5, StatisticalAnalysis.calculateMean(values3), 0.01);
        
        // Test empty array
        double[] values4 = {};
        assertEquals(0.0, StatisticalAnalysis.calculateMean(values4), 0.01);
        
        // Test null array
        assertEquals(0.0, StatisticalAnalysis.calculateMean(null), 0.01);
        
        // Test single element
        double[] values5 = {42.0};
        assertEquals(42.0, StatisticalAnalysis.calculateMean(values5), 0.01);
    }

    // Test calculateStandardDeviation
    @Test
    void testCalculateStandardDeviation() {
        // Test normal case (standard deviation of {1,2,3,4,5} = sqrt(2.5) ≈ 1.58)
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        assertEquals(1.58, StatisticalAnalysis.calculateStandardDeviation(values1), 0.01);
        
        // Test with same values (std dev = 0)
        double[] values2 = {5.0, 5.0, 5.0};
        assertEquals(0.0, StatisticalAnalysis.calculateStandardDeviation(values2), 0.01);
        
        // Test empty array
        double[] values3 = {};
        assertEquals(0.0, StatisticalAnalysis.calculateStandardDeviation(values3), 0.01);
        
        // Test null array
        assertEquals(0.0, StatisticalAnalysis.calculateStandardDeviation(null), 0.01);
        
        // Test single element
        double[] values4 = {10.0};
        assertEquals(0.0, StatisticalAnalysis.calculateStandardDeviation(values4), 0.01);
    }

    // Test calculateVariance
    @Test
    void testCalculateVariance() {
        // Test normal case (variance of {1,2,3,4,5} = 2.5)
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        assertEquals(2.5, StatisticalAnalysis.calculateVariance(values1), 0.01);
        
        // Test with same values (variance = 0)
        double[] values2 = {3.0, 3.0, 3.0};
        assertEquals(0.0, StatisticalAnalysis.calculateVariance(values2), 0.01);
        
        // Test empty array
        double[] values3 = {};
        assertEquals(0.0, StatisticalAnalysis.calculateVariance(values3), 0.01);
        
        // Test null array
        assertEquals(0.0, StatisticalAnalysis.calculateVariance(null), 0.01);
        
        // Test single element
        double[] values4 = {7.0};
        assertEquals(0.0, StatisticalAnalysis.calculateVariance(values4), 0.01);
    }

    // Test calculateMedian
    @Test
    void testCalculateMedian() {
        // Test odd number of elements
        double[] values1 = {1.0, 3.0, 5.0, 7.0, 9.0};
        assertEquals(5.0, StatisticalAnalysis.calculateMedian(values1), 0.01);
        
        // Test even number of elements (average of two middle values)
        double[] values2 = {1.0, 2.0, 3.0, 4.0};
        assertEquals(2.5, StatisticalAnalysis.calculateMedian(values2), 0.01);
        
        // Test unsorted array
        double[] values3 = {9.0, 1.0, 5.0, 3.0, 7.0};
        assertEquals(5.0, StatisticalAnalysis.calculateMedian(values3), 0.01);
        
        // Test empty array
        double[] values4 = {};
        assertEquals(0.0, StatisticalAnalysis.calculateMedian(values4), 0.01);
        
        // Test null array
        assertEquals(0.0, StatisticalAnalysis.calculateMedian(null), 0.01);
        
        // Test single element
        double[] values5 = {42.0};
        assertEquals(42.0, StatisticalAnalysis.calculateMedian(values5), 0.01);
    }

    // Test calculateMode
    @Test
    void testCalculateMode() {
        // Test with clear mode
        double[] values1 = {1.0, 2.0, 2.0, 3.0, 4.0};
        assertEquals(2.0, StatisticalAnalysis.calculateMode(values1), 0.01);
        
        // Test with multiple occurrences
        double[] values2 = {1.0, 1.0, 1.0, 2.0, 2.0, 3.0};
        assertEquals(1.0, StatisticalAnalysis.calculateMode(values2), 0.01);
        
        // Test with all unique values (no mode)
        double[] values3 = {1.0, 2.0, 3.0, 4.0, 5.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateMode(values3)));
        
        // Test empty array
        double[] values4 = {};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateMode(values4)));
        
        // Test null array
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateMode(null)));
        
        // Test single element
        double[] values5 = {5.0};
        assertEquals(5.0, StatisticalAnalysis.calculateMode(values5), 0.01);
    }

    // Test calculateCorrelation
    @Test
    void testCalculateCorrelation() {
        // Test perfect positive correlation
        double[] x1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y1 = {2.0, 4.0, 6.0, 8.0, 10.0};
        assertEquals(1.0, StatisticalAnalysis.calculateCorrelation(x1, y1), 0.01);
        
        // Test perfect negative correlation
        double[] x2 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y2 = {10.0, 8.0, 6.0, 4.0, 2.0};
        assertEquals(-1.0, StatisticalAnalysis.calculateCorrelation(x2, y2), 0.01);
        
        // Test no correlation
        double[] x3 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y3 = {5.0, 3.0, 1.0, 3.0, 5.0};
        double correlation = StatisticalAnalysis.calculateCorrelation(x3, y3);
        assertTrue(Math.abs(correlation) < 0.5); // Should be close to 0
        
        // Test null arrays
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCorrelation(null, y1)));
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCorrelation(x1, null)));
        
        // Test mismatched lengths
        double[] x4 = {1.0, 2.0, 3.0};
        double[] y4 = {1.0, 2.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCorrelation(x4, y4)));
        
        // Test single element
        double[] x5 = {1.0};
        double[] y5 = {2.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCorrelation(x5, y5)));
    }

    // Test calculateRange
    @Test
    void testCalculateRange() {
        // Test normal case
        double[] values1 = {1.0, 5.0, 3.0, 9.0, 2.0};
        assertEquals(8.0, StatisticalAnalysis.calculateRange(values1), 0.01);
        
        // Test with negative values
        double[] values2 = {-5.0, -1.0, 0.0, 3.0};
        assertEquals(8.0, StatisticalAnalysis.calculateRange(values2), 0.01);
        
        // Test with same values
        double[] values3 = {5.0, 5.0, 5.0};
        assertEquals(0.0, StatisticalAnalysis.calculateRange(values3), 0.01);
        
        // Test empty array
        double[] values4 = {};
        assertEquals(0.0, StatisticalAnalysis.calculateRange(values4), 0.01);
        
        // Test null array
        assertEquals(0.0, StatisticalAnalysis.calculateRange(null), 0.01);
        
        // Test single element
        double[] values5 = {10.0};
        assertEquals(0.0, StatisticalAnalysis.calculateRange(values5), 0.01);
    }

    // Test calculateQuartiles
    @Test
    void testCalculateQuartiles() {
        // Test normal case
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        double[] quartiles1 = StatisticalAnalysis.calculateQuartiles(values1);
        assertNotNull(quartiles1);
        assertEquals(3, quartiles1.length);
        assertEquals(2.5, quartiles1[0], 0.01); // Q1
        assertEquals(5.0, quartiles1[1], 0.01); // Q2 (median)
        assertEquals(7.5, quartiles1[2], 0.01); // Q3
        
        // Test with even number of elements
        double[] values2 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] quartiles2 = StatisticalAnalysis.calculateQuartiles(values2);
        assertNotNull(quartiles2);
        assertEquals(3, quartiles2.length);
        
        // Test null array
        assertNull(StatisticalAnalysis.calculateQuartiles(null));
        
        // Test empty array
        double[] values3 = {};
        assertNull(StatisticalAnalysis.calculateQuartiles(values3));
        
        // Test insufficient elements
        double[] values4 = {1.0, 2.0};
        assertNull(StatisticalAnalysis.calculateQuartiles(values4));
    }

    // Test calculateIQR
    @Test
    void testCalculateIQR() {
        // Test normal case
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        double iqr1 = StatisticalAnalysis.calculateIQR(values1);
        assertEquals(5.0, iqr1, 0.01); // Q3 - Q1 = 7.5 - 2.5 = 5.0
        
        // Test null array
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateIQR(null)));
        
        // Test insufficient elements
        double[] values2 = {1.0, 2.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateIQR(values2)));
    }

    // Test identifyOutliers
    @Test
    void testIdentifyOutliers() {
        // Test with outliers
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 100.0};
        List<Double> outliers1 = StatisticalAnalysis.identifyOutliers(values1);
        assertFalse(outliers1.isEmpty());
        assertTrue(outliers1.contains(100.0));
        
        // Test without outliers
        double[] values2 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        List<Double> outliers2 = StatisticalAnalysis.identifyOutliers(values2);
        assertTrue(outliers2.isEmpty());
        
        // Test null array
        List<Double> outliers3 = StatisticalAnalysis.identifyOutliers(null);
        assertTrue(outliers3.isEmpty());
        
        // Test insufficient elements
        double[] values4 = {1.0, 2.0, 3.0};
        List<Double> outliers4 = StatisticalAnalysis.identifyOutliers(values4);
        assertTrue(outliers4.isEmpty());
    }

    // Test calculateLinearRegression
    @Test
    void testCalculateLinearRegression() {
        // Test perfect linear relationship (y = 2x + 1)
        double[] x1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y1 = {3.0, 5.0, 7.0, 9.0, 11.0};
        double[] regression1 = StatisticalAnalysis.calculateLinearRegression(x1, y1);
        assertNotNull(regression1);
        assertEquals(2, regression1.length);
        assertEquals(2.0, regression1[0], 0.01); // slope
        assertEquals(1.0, regression1[1], 0.01); // intercept
        
        // Test null arrays
        assertNull(StatisticalAnalysis.calculateLinearRegression(null, y1));
        assertNull(StatisticalAnalysis.calculateLinearRegression(x1, null));
        
        // Test mismatched lengths
        double[] x2 = {1.0, 2.0, 3.0};
        double[] y2 = {1.0, 2.0};
        assertNull(StatisticalAnalysis.calculateLinearRegression(x2, y2));
        
        // Test single element
        double[] x3 = {1.0};
        double[] y3 = {2.0};
        assertNull(StatisticalAnalysis.calculateLinearRegression(x3, y3));
        
        // Test all x values same
        double[] x4 = {5.0, 5.0, 5.0};
        double[] y4 = {1.0, 2.0, 3.0};
        assertNull(StatisticalAnalysis.calculateLinearRegression(x4, y4));
    }

    // Test calculateCoefficientOfVariation
    @Test
    void testCalculateCoefficientOfVariation() {
        // Test normal case
        double[] values1 = {10.0, 20.0, 30.0, 40.0, 50.0};
        double cv1 = StatisticalAnalysis.calculateCoefficientOfVariation(values1);
        assertFalse(Double.isNaN(cv1));
        assertTrue(cv1 > 0);
        
        // Test with zero mean
        double[] values2 = {0.0, 0.0, 0.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCoefficientOfVariation(values2)));
        
        // Test null array
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCoefficientOfVariation(null)));
        
        // Test empty array
        double[] values3 = {};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateCoefficientOfVariation(values3)));
    }

    // Test calculateMovingAverage
    @Test
    void testCalculateMovingAverage() {
        // Test normal case with window size 3
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] movingAvg1 = StatisticalAnalysis.calculateMovingAverage(values1, 3);
        assertNotNull(movingAvg1);
        assertEquals(4, movingAvg1.length); // 6 - 3 + 1 = 4
        assertEquals(2.0, movingAvg1[0], 0.01); // (1+2+3)/3
        assertEquals(3.0, movingAvg1[1], 0.01); // (2+3+4)/3
        assertEquals(4.0, movingAvg1[2], 0.01); // (3+4+5)/3
        assertEquals(5.0, movingAvg1[3], 0.01); // (4+5+6)/3
        
        // Test with window size equal to array length
        double[] values2 = {1.0, 2.0, 3.0};
        double[] movingAvg2 = StatisticalAnalysis.calculateMovingAverage(values2, 3);
        assertNotNull(movingAvg2);
        assertEquals(1, movingAvg2.length);
        assertEquals(2.0, movingAvg2[0], 0.01);
        
        // Test null array
        assertNull(StatisticalAnalysis.calculateMovingAverage(null, 3));
        
        // Test empty array
        double[] values3 = {};
        assertNull(StatisticalAnalysis.calculateMovingAverage(values3, 3));
        
        // Test invalid window size
        double[] values4 = {1.0, 2.0, 3.0};
        assertNull(StatisticalAnalysis.calculateMovingAverage(values4, 0));
        assertNull(StatisticalAnalysis.calculateMovingAverage(values4, -1));
        assertNull(StatisticalAnalysis.calculateMovingAverage(values4, 10));
    }

    // Test calculateZScore
    @Test
    void testCalculateZScore() {
        // Test normal case
        double[] values1 = {10.0, 20.0, 30.0, 40.0, 50.0};
        double zScore1 = StatisticalAnalysis.calculateZScore(30.0, values1);
        assertEquals(0.0, zScore1, 0.01); // Mean is 30, so z-score of 30 is 0
        
        // Test value above mean
        double zScore2 = StatisticalAnalysis.calculateZScore(50.0, values1);
        assertTrue(zScore2 > 0);
        
        // Test value below mean
        double zScore3 = StatisticalAnalysis.calculateZScore(10.0, values1);
        assertTrue(zScore3 < 0);
        
        // Test null array
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateZScore(10.0, null)));
        
        // Test empty array
        double[] values2 = {};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateZScore(10.0, values2)));
        
        // Test with zero standard deviation
        double[] values3 = {5.0, 5.0, 5.0};
        assertTrue(Double.isNaN(StatisticalAnalysis.calculateZScore(5.0, values3)));
    }

    // Test generateStatisticalSummary
    @Test
    void testGenerateStatisticalSummary() {
        // Test normal case
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        String summary1 = StatisticalAnalysis.generateStatisticalSummary(values1);
        assertNotNull(summary1);
        assertFalse(summary1.isEmpty());
        assertTrue(summary1.contains("Statistical Summary"));
        assertTrue(summary1.contains("Count: 9"));
        assertTrue(summary1.contains("Mean"));
        assertTrue(summary1.contains("Median"));
        assertTrue(summary1.contains("Standard Deviation"));
        
        // Test empty array
        double[] values2 = {};
        String summary2 = StatisticalAnalysis.generateStatisticalSummary(values2);
        assertEquals("No data available for statistical analysis.", summary2);
        
        // Test null array
        String summary3 = StatisticalAnalysis.generateStatisticalSummary(null);
        assertEquals("No data available for statistical analysis.", summary3);
    }

    // Integration test: Test that methods work together correctly
    @Test
    void testIntegration() {
        double[] values = {10.0, 20.0, 30.0, 40.0, 50.0};
        
        double mean = StatisticalAnalysis.calculateMean(values);
        double stdDev = StatisticalAnalysis.calculateStandardDeviation(values);
        double variance = StatisticalAnalysis.calculateVariance(values);
        
        // Verify relationship: variance = stdDev^2
        assertEquals(variance, stdDev * stdDev, 0.01);
        
        // Verify mean is correct
        assertEquals(30.0, mean, 0.01);
        
        // Verify median is in the middle
        double median = StatisticalAnalysis.calculateMedian(values);
        assertEquals(30.0, median, 0.01);
        
        // Verify range
        double range = StatisticalAnalysis.calculateRange(values);
        assertEquals(40.0, range, 0.01);
    }
}

