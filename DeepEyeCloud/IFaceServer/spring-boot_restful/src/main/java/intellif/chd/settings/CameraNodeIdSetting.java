package intellif.chd.settings;

public class CameraNodeIdSetting {
	private static long nodeId;

	public static long getNodeId() {
		return nodeId;
	}

	public static void setNodeId(long nodeId) {
		CameraNodeIdSetting.nodeId = nodeId;
	}

}
