package zhuboss.gateway.facade.vo;

import java.io.Serializable;
import java.util.List;

public class GridTable<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	private List<T> rows;
	
	private Integer total;
	
	public GridTable(){
		
	}
	
	public GridTable(List<T> rows, Integer total) {
		this.rows = rows;
		this.total = total;
	}
	
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
}
