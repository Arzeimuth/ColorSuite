package buxburt.anddev.colorharmonizer.helper;

/**
 * A color message is used by classes that are speed intensive.
 * 
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public interface ColorMessage {
	
	//all classes implementing this will be able to update messages seemlessly
	void sendMessage();
}
