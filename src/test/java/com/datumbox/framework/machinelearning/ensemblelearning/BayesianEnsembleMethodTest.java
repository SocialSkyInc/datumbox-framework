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
package com.datumbox.framework.machinelearning.ensemblelearning;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.framework.machinelearning.datatransformation.DummyXYMinMaxNormalizer;
import com.datumbox.tests.utilities.TestUtils;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class BayesianEnsembleMethodTest {
    
    public BayesianEnsembleMethodTest() {
    }


    /**
     * Test of predict method, of class BayesianEnsembleMethod.
     */
    @Test
    public void testValidate() {
        TestUtils.log(this.getClass(), "validate");
        
        Dataset trainingData = new Dataset(TestUtils.getDBConfig());
        trainingData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));

        trainingData.add(Record.newDataVector(new String[] {"pos","neg"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","neg"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"pos","neg"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"neg","pos"}, "pos"));
        trainingData.add(Record.newDataVector(new String[] {"neg","pos"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"neg","pos"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"pos","neg"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"pos","neg"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"neg","neg"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"neg","neg"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"neg","neg"}, "neg"));
        trainingData.add(Record.newDataVector(new String[] {"neg","neg"}, "neg"));
        
        
        Dataset validationData = new Dataset(TestUtils.getDBConfig());
        validationData.add(Record.newDataVector(new String[] {"pos","pos"}, "pos"));
        validationData.add(Record.newDataVector(new String[] {"pos","neg"}, "pos"));
        validationData.add(Record.newDataVector(new String[] {"neg","pos"}, "neg"));
        validationData.add(Record.newDataVector(new String[] {"neg","neg"}, "neg"));
        
        
        
        String dbName = "JUnitBayesianEnsembleMethod";
        
        DummyXYMinMaxNormalizer df = new DummyXYMinMaxNormalizer(dbName, TestUtils.getDBConfig());
        df.fit_transform(trainingData, new DummyXYMinMaxNormalizer.TrainingParameters());
        
        df.transform(validationData);
        
        
        BayesianEnsembleMethod instance = new BayesianEnsembleMethod(dbName, TestUtils.getDBConfig());
        
        BayesianEnsembleMethod.TrainingParameters param = new BayesianEnsembleMethod.TrainingParameters();
        
        instance.fit(trainingData, param);
        
        
        instance = null;
        instance = new BayesianEnsembleMethod(dbName, TestUtils.getDBConfig());
        
        instance.validate(validationData);
        
        df.denormalize(trainingData);
        df.denormalize(validationData);
        df.erase();
        
        Map<Integer, Object> expResult = new HashMap<>();
        Map<Integer, Object> result = new HashMap<>();
        for(Integer rId : validationData) {
            Record r = validationData.get(rId);
            expResult.put(rId, r.getY());
            result.put(rId, r.getYPredicted());
        }
        assertEquals(expResult, result);
        
        instance.erase();
    }

}
