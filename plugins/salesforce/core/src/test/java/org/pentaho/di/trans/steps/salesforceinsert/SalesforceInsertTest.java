/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.salesforceinsert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.sforce.ws.bind.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.salesforce.SalesforceConnection;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.wsdl.Constants;

public class SalesforceInsertTest {

  private static final String ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT = "Account:ExtID_AccountId__c/Account";
  private static final String ACCOUNT_ID = "AccountId";
  private StepMockHelper<SalesforceInsertMeta, SalesforceInsertData> smh;

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
    PluginRegistry.init();
    String passwordEncoderPluginID =
        Const.NVL( EnvUtil.getSystemProperty( Const.KETTLE_PASSWORD_ENCODER_PLUGIN ), "Kettle" );
    Encr.init( passwordEncoderPluginID );
  }

  @Before
  public void setUp() throws Exception {
    smh =
      new StepMockHelper<SalesforceInsertMeta, SalesforceInsertData>( "SalesforceInsert", SalesforceInsertMeta.class,
        SalesforceInsertData.class );
    when( smh.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) ).thenReturn(
      smh.logChannelInterface );
  }

  @Test
  public void testWriteToSalesForceForNullExtIdField_WithExtIdNO() throws Exception {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta = generateSalesforceInsertMeta( new String[] { ACCOUNT_ID }, new Boolean[] { false } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaString( "AccNoExtId" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    sfInputStep.writeToSalesForce( new Object[] { null } );
    assertEquals( 1, data.sfBuffer[0].getFieldsToNull().length );
    assertEquals( ACCOUNT_ID, data.sfBuffer[0].getFieldsToNull()[0] );
    assertNull( SalesforceConnection.getChildren( data.sfBuffer[0] ) );
  }

  @Test
  public void testWriteToSalesForceForNullExtIdField_WithExtIdYES() throws Exception {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta =
      generateSalesforceInsertMeta( new String[] { ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT }, new Boolean[] { true } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaString( "AccExtId" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    sfInputStep.writeToSalesForce( new Object[] { null } );
    assertEquals( 1, data.sfBuffer[0].getFieldsToNull().length );
    assertEquals( ACCOUNT_ID, data.sfBuffer[0].getFieldsToNull()[0] );
    assertNull( SalesforceConnection.getChildren( data.sfBuffer[0] ) );
  }

  @Test
  public void testWriteToSalesForceForNotNullExtIdField_WithExtIdNO() throws Exception {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta = generateSalesforceInsertMeta( new String[] { ACCOUNT_ID }, new Boolean[] { false } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaString( "AccNoExtId" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    sfInputStep.writeToSalesForce( new Object[] { "001i000001c5Nv9AAE" } );
    assertEquals( 0, data.sfBuffer[0].getFieldsToNull().length );
    assertEquals( 1, SalesforceConnection.getChildren( data.sfBuffer[0] ).length );
    assertEquals( Constants.PARTNER_SOBJECT_NS,
      SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getName().getNamespaceURI() );
    assertEquals( ACCOUNT_ID, SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getName().getLocalPart() );
    assertEquals( "001i000001c5Nv9AAE", SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getValue() );
    assertFalse( SalesforceConnection.getChildren( data.sfBuffer[0] )[0].hasChildren() );
  }

  @Test
  public void testWriteToSalesForceForNotNullExtIdField_WithExtIdYES() throws Exception {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta =
      generateSalesforceInsertMeta( new String[] { ACCOUNT_EXT_ID_ACCOUNT_ID_C_ACCOUNT }, new Boolean[] { true } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaString( "AccExtId" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    sfInputStep.writeToSalesForce( new Object[] { "tkas88" } );
    assertEquals( 0, data.sfBuffer[0].getFieldsToNull().length );
    assertEquals( 1, SalesforceConnection.getChildren( data.sfBuffer[0] ).length );
    assertEquals( Constants.PARTNER_SOBJECT_NS,
      SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getName().getNamespaceURI() );
    assertEquals( "Account", SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getName().getLocalPart() );
    assertNull( SalesforceConnection.getChildren( data.sfBuffer[0] )[0].getValue() );
    assertFalse( SalesforceConnection.getChildren( data.sfBuffer[0] )[0].hasChildren() );
  }

  @Test
  public void testLogMessageInDetailedModeFotWriteToSalesForce() throws KettleException {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta = generateSalesforceInsertMeta( new String[] { ACCOUNT_ID }, new Boolean[] { false } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );
    when( sfInputStep.getLogChannel().isDetailed() ).thenReturn( true );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaString( "AccNoExtId" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    verify( sfInputStep.getLogChannel(), never() ).logDetailed( anyString() );
    sfInputStep.writeToSalesForce( new Object[] { "001i000001c5Nv9AAE" } );
    verify( sfInputStep.getLogChannel() ).logDetailed( "Called writeToSalesForce with 0 out of 2" );
  }

  private SalesforceInsertData generateSalesforceInsertData() {
    SalesforceInsertData data = smh.initStepDataInterface;
    data.nrfields = 1;
    data.fieldnrs = new int[] { 0 };
    data.sfBuffer = new SObject[] { null };
    data.outputBuffer = new Object[][] { null };
    return data;
  }

  private SalesforceInsertMeta generateSalesforceInsertMeta( String[] updateLookup, Boolean[] useExternalId ) {
    SalesforceInsertMeta meta = smh.initStepMetaInterface;
    doReturn( 2 ).when( meta ).getBatchSizeInt();
    doReturn( updateLookup ).when( meta ).getUpdateLookup();
    doReturn( useExternalId ).when( meta ).getUseExternalId();
    doReturn( UUID.randomUUID().toString() ).when( meta ).getTargetURL();
    doReturn( UUID.randomUUID().toString() ).when( meta ).getUsername();
    doReturn( UUID.randomUUID().toString() ).when( meta ).getPassword();
    doReturn( UUID.randomUUID().toString() ).when( meta ).getModule();
    doReturn( UUID.randomUUID().toString() ).when( meta ).getSalesforceIDFieldName();
    return meta;
  }

  @Test
  public void testWriteToSalesForcePentahoIntegerValue() throws Exception {
    SalesforceInsert sfInputStep =
      new SalesforceInsert( smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans );
    SalesforceInsertMeta meta =
      generateSalesforceInsertMeta( new String[] { ACCOUNT_ID }, new Boolean[] { false } );
    SalesforceInsertData data = generateSalesforceInsertData();
    sfInputStep.init( meta, data );

    RowMeta rowMeta = new RowMeta();
    ValueMetaBase valueMeta = new ValueMetaInteger( "IntValue" );
    rowMeta.addValueMeta( valueMeta );
    smh.initStepDataInterface.inputRowMeta = rowMeta;

    sfInputStep.writeToSalesForce( new Object[] { 1L } );
    XmlObject sObject = data.sfBuffer[ 0 ].getChild( ACCOUNT_ID );
    Assert.assertEquals( sObject.getValue(), 1 );
  }
}
