package org.genericsystem.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.core.Snapshot;
import org.genericsystem.snapshot.AbstractSequentiableSnapshot;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class SnapshotTest extends AbstractTest {

	private Snapshot<String> snapshot;

	private Snapshot<String> snapshotEmpty;

	private Snapshot<String> snapshotSequentiable;

	@BeforeTest
	public void init() {
		snapshot = new AbstractSnapshot<String>() {
			@Override
			public Iterator<String> iterator() {
				List<String> elements = new ArrayList<>();
				elements.add("toto");
				elements.add("tata");
				elements.add("titi");
				return elements.iterator();
			}
		};
		snapshotEmpty = new AbstractSnapshot<String>() {
			@Override
			public Iterator<String> iterator() {
				return new ArrayList<String>().iterator();
			}
		};
		snapshotSequentiable = new AbstractSequentiableSnapshot<String>() {
			@Override
			public Iterator<String> sequentiableIterator() {
				List<String> elements = new ArrayList<>();
				elements.add("toto");
				elements.add("tata");
				elements.add("titi");
				return elements.iterator();
			}
		};
	}

	public void testIsEmpty() {
		assert snapshotEmpty.isEmpty();
		assert !snapshot.isEmpty();
	}

	public void testSize() {
		assert snapshotEmpty.size() == 0;
		assert snapshot.size() == 3;
	}

	public void testContains() {
		assert !snapshotEmpty.contains("tata");
		assert snapshot.contains("tata");
	}

	public void testGet() {
		assert snapshot.get(1).equals("tata");
		assert snapshot.get(3) == null;
	}

	public void testGetSequentiable() {
		assert snapshotSequentiable.get(0).equals("toto");
		assert snapshotSequentiable.get(1).equals("tata");
		assert snapshot.get(3) == null;
	}

	public void testGetSequentiableWithEqualsIndex() {
		assert snapshotSequentiable.get(0).equals("toto");
		assert snapshotSequentiable.get(0).equals("toto");
	}

	public void testGetSequentiableWithJump() {
		assert snapshotSequentiable.get(0).equals("toto");
		assert snapshotSequentiable.get(2).equals("titi");
	}

}
