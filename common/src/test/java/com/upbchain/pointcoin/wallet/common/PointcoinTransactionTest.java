package com.upbchain.pointcoin.wallet.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.validation.constraints.AssertFalse;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PointcoinTransactionTest {

    @Before
    public void setup() {
    }

    @Test
    public void generalTest() {
        ObjectMapper mapper = new ObjectMapper();

        PointcoinTransaction coinbasedTx1Inner = null;
        try {
            coinbasedTx1Inner = mapper.readValue(this.getClass().getResourceAsStream("coinbased-tx1-inner.json"), new TypeReference<PointcoinTransaction>() {
            });

            assertEquals(177, coinbasedTx1Inner.getConfirmations());
            assertEquals(1482967202L, coinbasedTx1Inner.getTime());
            assertEquals(1482967202L, coinbasedTx1Inner.getTimereceived());
            assertEquals("9fbe17eb36267122e91678256e95c3e9b332cb21914ec837980f652dd41f4913", coinbasedTx1Inner.getTxId());
            assertEquals(0, coinbasedTx1Inner.getTxInputs().size());
            assertEquals(1, coinbasedTx1Inner.getTxOutputs().size());
            coinbasedTx1Inner.getTxOutputs()
                    .forEach(
                            it -> assertTrue(it.getCurrency().equals("XPP") && it.getIndex() == 0
                                    && it.getAddresses().get(0).equals("UPyPL1EJ7TZXv6GPwSFbqj4uE7MUwMjwFiQ")
                                    && it.getValue().equals(BigDecimal.valueOf(264.00877641))));
            assertTrue(coinbasedTx1Inner.isCoinbased());
            assertTrue(coinbasedTx1Inner.isGenerated());
        } catch (IOException ex) {
            fail("can't load coinbased-tx1-inner.json.json with exception: " + ex.getMessage());
        }

        PointcoinTransaction coinbasedTx1Outer = null;
        try {
            coinbasedTx1Outer = mapper.readValue(this.getClass().getResourceAsStream("coinbased-tx1-outer.json"), new TypeReference<PointcoinTransaction>() {
            });

            assertEquals(178, coinbasedTx1Outer.getConfirmations());
            
            assertTrue(coinbasedTx1Outer.isCoinbased());
            assertTrue(coinbasedTx1Outer.isGenerated());

        } catch (IOException ex) {
            fail("can't load coinbased-tx1-outer.json.json with exception: " + ex.getMessage());
        }

        assertTrue("coinbasedTx1Inner should match with coinbasedTx1Outer", shouldMatch(coinbasedTx1Inner, coinbasedTx1Outer));

        PointcoinTransaction mulitinoutTx1Confirmed = null;
        try {
            mulitinoutTx1Confirmed = mapper.readValue(this.getClass().getResourceAsStream("multi-in-out-tx1-confirmed.json"),
                    new TypeReference<PointcoinTransaction>() {
                    });

            assertEquals(2, mulitinoutTx1Confirmed.getTxInputs().size());

            PointcoinTransactionInput input = mulitinoutTx1Confirmed.getTxInputs().get(0);
            Arrays.asList(input).forEach(
                    it -> assertTrue(it.getTxId().equals("b70729e2310131d0576f810f6c11bf8a429820d1fa294de6b4d18e391b53988a") && it.getVout() == 1
                            && it.getAddresses().get(0).equals("UPoR7XjJruczJzXErEWb6TdG6cGSSpHpXXU") && it.getValue().equals(BigDecimal.valueOf(0.85))));

            input = mulitinoutTx1Confirmed.getTxInputs().get(1);
            Arrays.asList(input).forEach(
                    it -> assertTrue(it.getTxId().equals("ef4510a7e99024ee2ab4d571974aa5502ffd627cffc9b8df237b163540a1ed51") && it.getVout() == 0
                            && it.getAddresses().get(0).equals("UPfmoucbdbdyVGJtNzrZQs2bYkitWTKtsy4") && it.getValue().equals(BigDecimal.valueOf(1.0))));

            assertEquals(2, mulitinoutTx1Confirmed.getTxOutputs().size());

            PointcoinTransactionOutput output = mulitinoutTx1Confirmed.getTxOutputs().get(0);
            Arrays.asList(output).forEach(
                    it -> assertTrue(it.getCurrency().equals("XPP") && it.getIndex() == 0
                            && it.getAddresses().get(0).equals("UPysVoaeHnhjYm9dxM5Txff7ZMf5BqjqtdK") && it.getValue().equals(BigDecimal.valueOf(0.7))));

            output = mulitinoutTx1Confirmed.getTxOutputs().get(1);
            Arrays.asList(output).forEach(
                    it -> assertTrue(it.getCurrency().equals("XPP") && it.getIndex() == 1
                            && it.getAddresses().get(0).equals("UPgEbL4UPv34hz45fz8PLALky8qzNVWqPLe") && it.getValue().equals(BigDecimal.valueOf(1.0))));
            assertTrue(!mulitinoutTx1Confirmed.isCoinbased());
            assertTrue(!mulitinoutTx1Confirmed.isGenerated());
        } catch (IOException ex) {
            fail("can't load multi-in-out-tx1-confirmed.json.json with exception: " + ex.getMessage());
        }

        PointcoinTransaction mulitinoutTx1Unconfirmed = null;
        try {
            mulitinoutTx1Unconfirmed = mapper.readValue(this.getClass().getResourceAsStream("multi-in-out-tx1-unconfirmed.json"),
                    new TypeReference<PointcoinTransaction>() {
                    });

            assertTrue(!mulitinoutTx1Unconfirmed.isCoinbased());
            assertTrue(!mulitinoutTx1Unconfirmed.isGenerated());
        } catch (IOException ex) {
            fail("can't load multi-in-out-tx1-unconfirmed.json.json with exception: " + ex.getMessage());
        }

        assertTrue("mulitinoutTx1Confirmed should match with mulitinoutTx1Unconfirmed", shouldMatch(mulitinoutTx1Confirmed, mulitinoutTx1Unconfirmed));
    }

    private boolean shouldMatch(PointcoinTransaction one, PointcoinTransaction another) {

        assertEquals(one.getTime(), another.getTime());
        assertEquals(one.getTimereceived(), another.getTimereceived());
        assertEquals(one.getTxId(), another.getTxId());
        assertEquals(one.getTxInputs(), another.getTxInputs());
        assertEquals(one.getTxOutputs(), another.getTxOutputs());
        return true;
    }
    
    @Test
    public void coinbasedWithVINsTest() {
        ObjectMapper mapper = new ObjectMapper();

        PointcoinTransaction coinbasedTx2Generated = null;
        try {
            coinbasedTx2Generated = mapper.readValue(this.getClass().getResourceAsStream("coinbased-tx2-generated.json"), new TypeReference<PointcoinTransaction>() {});

            assertEquals(1, coinbasedTx2Generated.getConfirmations());
            assertEquals(1483884750L, coinbasedTx2Generated.getTime());
            assertEquals(1483884750L, coinbasedTx2Generated.getTimereceived());
            assertEquals("78d09283c7b7925e7717b4d7601090f934c987926e259239a898389b57703f3f", coinbasedTx2Generated.getTxId());
            assertEquals(1, coinbasedTx2Generated.getTxInputs().size());
            assertEquals(3, coinbasedTx2Generated.getTxOutputs().size());
            coinbasedTx2Generated.getTxOutputs().forEach(it -> {

                assertTrue(it.getCurrency().equals("XPP"));

                if (it.getIndex() == 0) {
                    assertTrue(it.getValue().compareTo(BigDecimal.valueOf(0)) == 0);
                    assertTrue(it.getAddresses().size() == 0);
                } else if (it.getIndex() == 1) {
                    assertTrue(it.getValue().compareTo(BigDecimal.valueOf(66.07)) == 0);
                    assertTrue(it.getAddresses().size() == 1 && it.getAddresses().get(0).equals("UPyPL1EJ7TZXv6GPwSFbqj4uE7MUwMjwFiQ"));
                } else if (it.getIndex() == 2) {
                    assertTrue(it.getValue().compareTo(BigDecimal.valueOf(66.0874378)) == 0);
                    assertTrue(it.getAddresses().size() == 1 && it.getAddresses().get(0).equals("UPyPL1EJ7TZXv6GPwSFbqj4uE7MUwMjwFiQ"));
                } else {
                    fail("shouldn't be here");
                }
            });
            
            assertTrue(coinbasedTx2Generated.isCoinbased());
            assertTrue(coinbasedTx2Generated.isGenerated());
        } catch (IOException ex) {
            fail("can't load coinbased-tx2-generated.json with exception: " + ex.getMessage());
        }

    }

}