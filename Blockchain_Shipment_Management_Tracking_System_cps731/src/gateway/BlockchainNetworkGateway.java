package gateway;

import external.BlockchainNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway / Indirection layer between controllers and the external
 * BlockchainNetwork.
 * This applies the GRASP Indirection pattern and hides low-level blockchain
 * details
 * from the rest of the system.
 */
public class BlockchainNetworkGateway {

    private final BlockchainNetwork blockchainNetwork;
    private boolean connected = false;

    public BlockchainNetworkGateway(BlockchainNetwork blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }

    /** Open connection to the blockchain network. */
    public boolean connect() {
        connected = blockchainNetwork.connect();
        return connected;
    }

    /** Send a transaction string to the blockchain. */
    public boolean sendTransaction(String data) {
        if (!connected) {
            return false;
        }
        return blockchainNetwork.storeTransaction(data);
    }

    /**
     * Query ledger entries related to a shipment.
     * Each entry is currently just a String; controllers decide how to interpret
     * it.
     */
    public List<String> queryLedger(String shipmentId) {
        if (!connected) {
            return new ArrayList<>();
        }
        return new ArrayList<>(blockchainNetwork.queryLedger(shipmentId));
    }

    /** Validate a block (very simple stub). */
    public boolean validateBlock(String blockHash) {
        if (!connected) {
            return false;
        }
        return blockchainNetwork.validateBlock(blockHash);
    }

    /** Disconnect from the blockchain. */
    public void disconnect() {
        blockchainNetwork.disconnect();
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
