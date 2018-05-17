package intellif.chd.consts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constant {
	public static final String T_NAME_FILTER_FACE = "t_filter_face";
	public final static int REAL_LENGTH_181 = 140;

public static final ForkJoinPool MINING_TASK_POOL = new ForkJoinPool();
	
	public static final ForkJoinPool MINING_TASK_LIMIT_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 8 + 2);

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// 每个线程分析的最小人脸数
	public static final int MIN_STEP = 10;

	public static final Map<Long, ForkJoinTask<?>> FORK_JOIN_TASK_MAP = new HashMap<Long, ForkJoinTask<?>>();
}
