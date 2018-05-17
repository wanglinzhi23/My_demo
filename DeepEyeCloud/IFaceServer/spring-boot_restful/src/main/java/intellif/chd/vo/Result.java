package intellif.chd.vo;

public interface Result<M, T> {

	/**
	 * 在返回给前端之前，整理数据
	 */
	M clean();

}
