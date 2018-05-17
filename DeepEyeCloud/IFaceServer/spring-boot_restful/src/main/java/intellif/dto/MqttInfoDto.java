package intellif.dto;

/**
 * The Class MqttInfoDto.
 */
public class MqttInfoDto {
	// LOG.info("readValueFromJson(TaskId):" + node.get("TaskId").toString());//
	// LOG.info("readValueFromJson(BlackId):" +
	// node.get("BlackId").toString());//
	// LOG.info("readValueFromJson(FaceId):" + node.get("FaceId").toString());//
	// LOG.info("readValueFromJson(Confidence):" +
	// node.get("Confidence").toString());//
	// LOG.info("readValueFromJson(Time):" + node.get("Time").toString());//
	// LOG.info("readValueFromJson(AlarmType):" +
	// node.get("AlarmType").toString());//
	private String TaskId;
	
	private String PersonId;

	public String getTaskId() {
		return TaskId;
	}

	public void setTaskId(String taskId) {
		TaskId = taskId;
	}

	private String BlackId;

	public String getBlackId() {
		return BlackId;
	}

	public void setBlackId(String blackId) {
		BlackId = blackId;
	}

	private String FaceId;

	public String getFaceId() {
		return FaceId;
	}

	public void setFaceId(String faceId) {
		FaceId = faceId;
	}

	private String Time;

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	private String AlarmType;

	public String getAlarmType() {
		return AlarmType;
	}

	public void setAlarmType(String alarmType) {
		AlarmType = alarmType;
	}

	private String Confidence;

	public String getConfidence() {
		return Confidence;
	}

	public void setConfidence(String confidence) {
		Confidence = confidence;
	}

	@Override
	public String toString() {
		return "AlarmType:" + this.getAlarmType() + ",BlackId:" + this.getBlackId() + ",Confidence:"
				+ this.getConfidence() + ",FaceId:" + this.getFaceId() + ",TaskId:" + this.getTaskId() + ",Time:"
				+ this.getTime();
	}

    public String getPersonId() {
        return PersonId;
    }

    public void setPersonId(String personId) {
        PersonId = personId;
    }
}
