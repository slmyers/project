package ObjectGenerator;

public class IntGenerator extends ColumnObject implements ObjectGenerator {

	@Override
	public Object gen() {
		// TODO Auto-generated method stub
		return new Integer(randomInt(1, 1000));
	}

}