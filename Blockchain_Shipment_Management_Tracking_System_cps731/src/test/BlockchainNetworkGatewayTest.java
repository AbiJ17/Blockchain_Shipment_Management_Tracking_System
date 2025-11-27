package test;

import external.BlockchainNetwork;
import gateway.BlockchainNetworkGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlockchainNetworkGatewayTest {

    private BlockchainNetworkGateway gateway;

    @BeforeEach
    void setup() {
        // Use the real BlockchainNetwork but always go through the gateway
        BlockchainNetwork network = new BlockchainNetwork();
        gateway = new BlockchainNetworkGateway(network);
    }

    @Test
    void testConnectAndDisconnect() {
        // initially disconnected
        assertFalse(gateway.isConnected());

        // connect should flip the flag to true
        assertTrue(gateway.connect());
        assertTrue(gateway.isConnected());

        // disconnect should flip it back
        gateway.disconnect();
        assertFalse(gateway.isConnected());
    }

    @Test
    void testSendTransactionRequiresConnect() {
        // When not connected, sending should fail
        assertFalse(gateway.sendTransaction("S100:CREATED"));

        // After connecting, sending should succeed
        assertTrue(gateway.connect());
        assertTrue(gateway.sendTransaction("S100:CREATED"));
    }

    @Test
    void testQueryLedger() {
        // When not connected, queryLedger must return an empty list
        List<String> beforeConnect = gateway.queryLedger("S200");
        assertNotNull(beforeConnect);
        assertTrue(beforeConnect.isEmpty());

        // After connecting and sending a transaction, queryLedger must still
        // return a non-null list (we don’t assume anything about contents,
        // because that’s handled inside BlockchainNetwork, not the gateway).
        gateway.connect();
        gateway.sendTransaction("S200:CREATED");

        List<String> afterConnect = gateway.queryLedger("S200");
        assertNotNull(afterConnect);
        // No assertion about size/contents because that depends on
        // BlockchainNetwork’s internal implementation.
    }

    @Test
    void testValidateBlockRequiresConnect() {
        // When disconnected, validateBlock must return false
        assertFalse(gateway.validateBlock("dummy-hash"));

        // After connecting, the call should not throw, and simply return whatever
        // BlockchainNetwork decides (true or false). We just assert that it runs.
        gateway.connect();
        assertDoesNotThrow(() -> gateway.validateBlock("dummy-hash"));
    }
}
