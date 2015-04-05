/**
 * Copyright (C) 2013-2015 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox.framework.machinelearning.featureselection.categorical;

import com.datumbox.common.dataobjects.AssociativeArray;
import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.utilities.PHPfunctions;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.TestConfiguration;
import com.datumbox.tests.utilities.TestUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class ChisquareSelectTest {
    
    public ChisquareSelectTest() {
    }

    public static Dataset generateDataset(int n) {
        Dataset data = new Dataset(TestUtils.getDBConfig());
        for(int i=0;i<n;++i) {
            AssociativeArray xData = new AssociativeArray();
            //important fields
            xData.put("high_paid", (PHPfunctions.mt_rand(0, 4)>3)?1:0);
            xData.put("has_boat", PHPfunctions.mt_rand(0, 1));
            xData.put("has_luxury_car", PHPfunctions.mt_rand(0, 1));
            xData.put("has_butler", PHPfunctions.mt_rand(0, 1));
            //xData.put("has_butler", (PHPfunctions.mt_rand(0, 1)==1)?"yes":"no");
            xData.put("has_pool", PHPfunctions.mt_rand(0, 1));
            
            //not important fields
            xData.put("has_tv", PHPfunctions.mt_rand(0, 1));
            xData.put("has_dog", PHPfunctions.mt_rand(0, 1));
            xData.put("has_cat", PHPfunctions.mt_rand(0, 1));
            xData.put("has_fish", PHPfunctions.mt_rand(0, 1));
            xData.put("random_field", (double)PHPfunctions.mt_rand(0, 1000));
            
            double richScore = xData.getDouble("has_boat")
                             + xData.getDouble("has_luxury_car")
                             //+ ((xData.get("has_butler").equals("yes"))?1.0:0.0)
                             + xData.getDouble("has_butler")
                             + xData.getDouble("has_pool");
            
            Boolean isRich=false;
            if(richScore>=2 || xData.getDouble("high_paid")==1.0) {
                isRich = true;
            }
            
            data.add(new Record(xData, isRich));
        }
        
        return data;
    }
    
    @Test
    public void testSelectFeatures() {
        TestUtils.log(this.getClass(), "selectFeatures");
        RandomValue.setRandomGenerator(new Random(42));
        
        String dbName = "JUnitChisquareFeatureSelection";
        
        
        
        ChisquareSelect.TrainingParameters param = new ChisquareSelect.TrainingParameters();
        param.setRareFeatureThreshold(2);
        param.setMaxFeatures(5);
        param.setIgnoringNumericalFeatures(false);
        param.setALevel(0.05);
        
        Dataset trainingData = generateDataset(1000);
        ChisquareSelect instance = new ChisquareSelect(dbName, TestUtils.getDBConfig());
        
        
        instance.fit(trainingData, param);
        instance = null;
        
        
        instance = new ChisquareSelect(dbName, TestUtils.getDBConfig());
        
        instance.transform(trainingData);
        
        Set<Object> expResult = new HashSet<>(Arrays.asList("high_paid", "has_boat", "has_luxury_car", "has_butler", "has_pool"));
        Set<Object> result = trainingData.getColumns().keySet();
        assertEquals(expResult, result);
        instance.erase();
    }
    
}
