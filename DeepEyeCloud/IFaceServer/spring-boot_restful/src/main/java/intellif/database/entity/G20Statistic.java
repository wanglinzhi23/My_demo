package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class G20Statistic implements Serializable{

	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private int type;
    
    @Column
    private int statistic;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatistic() {
		return statistic;
	}

	public void setStatistic(int statistic) {
		this.statistic = statistic;
	}
    
    
}
