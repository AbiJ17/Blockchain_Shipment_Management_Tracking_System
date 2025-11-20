package external;

import java.util.List;

import org.w3c.dom.Node;

public class BlockchainNetwork {

    public List<Node> nodeList; 
    public String consensusAlgorithm;
   
    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }
    public String getConsensusAlgorithm() {
        return consensusAlgorithm;
    }
    public void setConsensusAlgorithm(String consensusAlgorithm) {
        this.consensusAlgorithm = consensusAlgorithm;
    } 

    
}
