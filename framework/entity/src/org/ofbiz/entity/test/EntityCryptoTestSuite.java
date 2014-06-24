/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.entity.test;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.testtools.EntityTestCase;

public class EntityCryptoTestSuite extends EntityTestCase {
    public EntityCryptoTestSuite(String name) {
        super(name);
    }

    public void testCryptoEncryption() throws Exception {
        // clear out all values
        delegator.removeByAnd("TestingCrypto", UtilMisc.toMap("testingCryptoTypeId", "BASIC"));

        String nanoTime = "" + System.nanoTime();

        // Ensure that null values are passed thru unencrypted.
        delegator.create("TestingCrypto", UtilMisc.toMap("testingCryptoId", "1", "testingCryptoTypeId", "BASIC"));
        GenericValue entity = delegator.findOne("TestingCrypto", UtilMisc.toMap("testingCryptoId", "1"), false);
        assertNull(entity.getString("unencryptedValue"));
        assertNull(entity.getString("encryptedValue"));
        GenericValue view = delegator.findOne("TestingCryptoRawView", UtilMisc.toMap("testingCryptoId", "1"), false);
        assertNull(view.getString("unencryptedValue"));
        assertNull(view.getString("encryptedValue"));
        assertNull(view.getString("rawEncryptedValue"));

        // Verify that encryption is taking place
        entity.setString("unencryptedValue", nanoTime);
        entity.setString("encryptedValue", nanoTime);
        entity.store();
        view.refresh();
        assertEquals(nanoTime, view.getString("unencryptedValue"));
        assertEquals(nanoTime, view.getString("encryptedValue"));

        String initialValue = view.getString("rawEncryptedValue");
        assertFalse(nanoTime.equals(initialValue));

        // Verify that the same value stored repeatedly gives different raw encrypted values.
        entity.setString("encryptedValue", nanoTime);
        entity.store();
        entity.refresh(); // this is a bug; store() ends up setting the encrypted value *into* the entity
        assertEquals(nanoTime, entity.getString("unencryptedValue"));
        assertEquals(nanoTime, entity.getString("encryptedValue"));

        view.refresh();
        assertEquals(nanoTime, view.getString("unencryptedValue"));
        assertEquals(nanoTime, view.getString("encryptedValue"));

        String updatedValue = view.getString("rawEncryptedValue");
        assertFalse(nanoTime.equals(updatedValue));
        assertFalse(initialValue.equals(updatedValue));
    }
}
