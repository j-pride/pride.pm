package util;

import pm.pride.util.generator.EntityGenerator;

public class EntityGeneratorWithExampleConfig extends EntityGenerator {

	@Override
	protected void createResourceAccessor() throws Exception {
		resourceAccessor = ResourceAccessorExampleConfig.initPriDE();
	}

	public EntityGeneratorWithExampleConfig(String[] args) throws Exception {
		super(args);
	}
	
	public static void main(String[] args) throws Exception {
		new EntityGeneratorWithExampleConfig(args).createAndPrint();
	}
}
