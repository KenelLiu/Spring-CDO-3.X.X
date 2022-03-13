package com.cdoPlugin.cdolib.database;

import com.cdoPlugin.cdolib.database.xsd.SQLTrans;

/**
 * @author KenelLiu
 */
public class TransDefine{

	private SQLTrans sqlTrans;	
	public SQLTrans getSqlTrans()
	{
		return sqlTrans;
	}

	public void setSqlTrans(SQLTrans sqlTrans)
	{
		this.sqlTrans=sqlTrans;
	}
	public TransDefine(){
		
	}

}
