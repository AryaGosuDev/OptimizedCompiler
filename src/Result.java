public class Result {
	
	int kind; //const, var, reg, condition
	int value; //value if it is a constant
	int address; //address if it is a variable
	int regno; // register number if it is a reg or a condition
	int cond, fixuplocation; // if it is a condition
	
	public Result ()
	{
		regno = -1;
	}
	
}
