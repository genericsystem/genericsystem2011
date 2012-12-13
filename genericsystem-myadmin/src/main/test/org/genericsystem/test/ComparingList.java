//package org.genericsystem.test;
//
//import java.util.AbstractSequentialList;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//@Test(invocationCount = 100)
//public class ComparingList {
//
//	private static final int MAX_SIZE = 15;
//	private static final int HIGHER = 50;
//	private static Logger log = LoggerFactory.getLogger(ComparingList.class);
//
//	private int listSize;
//	private List<Integer> classicList;
//	private List<Integer> gsList;
//
//	@BeforeClass
//	public void constructLists() {
//		this.listSize = (int) (Math.random() * MAX_SIZE);
//
//		this.classicList = new LinkedList<Integer>();
//		final PseudoConcurrentCollection<Integer> gsCollection = new PseudoConcurrentCollection<Integer>();
//
//		int i = 0;
//		while (i < listSize) {
//			int next = (int) (Math.random() * HIGHER);
//			if (!gsCollection.contains(next)) {
//				i++;
//				classicList.add(next);
//				gsCollection.add(next);
//			}
//		}
//
//		this.gsList = new AbstractSequentialList<Integer>() {
//			@Override
//			public Iterator<Integer> iterator() {
//				return gsCollection.iterator();
//			}
//		};
//
//		log.info("Comparing");
//		log.info("Classic List: " + classicList);
//		log.info("Gs List:      " + gsList);
//	}
//
//	@Test
//	public void compareIterations() {
//		Iterator<Integer> classicIterator = classicList.iterator();
//		Iterator<Integer> gsIterator = gsList.iterator();
//
//		while (classicIterator.hasNext()) {
//			if (!gsIterator.hasNext())
//				throw new NotEqualsException("Classic List has more elements. Next element: " + classicIterator.next());
//
//			Integer classicNext = classicIterator.next();
//			Integer gsNext = gsIterator.next();
//			if (!gsNext.equals(classicNext))
//				throw new NotEqualsException("Elements not equals. Classic: " + classicNext + ", Gs: " + gsNext);
//		}
//
//		if (gsIterator.hasNext()) {
//			throw new NotEqualsException("GS List has more elements. Next element: " + gsIterator.next());
//		}
//	}
//
//	@Test(invocationCount = 100)
//	public void compareGets() {
//		int index = (int) ((Math.random() * (listSize + 2)) - 2);
//		log.info("Testing get for index: " + index);
//
//		Exception classicException = null;
//		Exception gsException = null;
//
//		Integer classicInteger = null;
//		Integer gsInteger = null;
//
//		try {
//			classicInteger = classicList.get(index);
//		} catch (Exception e) {
//			classicException = e;
//		}
//
//		try {
//			gsInteger = gsList.get(index);
//		} catch (Exception e) {
//			gsException = e;
//		}
//
//		if (classicInteger != null) {
//			if (gsInteger == null) {
//				if (gsException != null)
//					throw new NotEqualsException("Gs List has thrown exception on index " + index + ". Classic List's get returned value: " + classicInteger.intValue() + "\nGsException: " + gsException);
//				else
//					throw new NotEqualsException("No Gs value and no exception. Impossible.");
//			}
//
//			if (!classicInteger.equals(gsInteger))
//				throw new NotEqualsException("Value for index " + index + "differ. Classic: " + classicInteger + ", Gs: " + gsInteger);
//			else
//				log.info("Equals for index " + index + ". Element: " + classicInteger);
//		} else {
//			if (gsInteger != null) {
//				if (classicException != null)
//					throw new NotEqualsException("Classic List has thrown exception on index " + index + ". GS List's get returned value: " + gsInteger + "\nClassic Exception: " + classicException);
//				else
//					throw new NotEqualsException("No classic value and no exception. Impossible.");
//			}
//
//			if (!gsException.getClass().equals(classicException.getClass()))
//				throw new NotEqualsException("Differents Exception for index " + index + ". Classic: " + classicException + ", Gs: " + gsException);
//			else
//				log.info("Equals for exceptions: " + index + ". Exception: " + classicException.getClass());
//		}
//	}
//
//	@SuppressWarnings("serial")
//	private class NotEqualsException extends RuntimeException {
//		@SuppressWarnings("unused")
//		public NotEqualsException() {
//		}
//
//		public NotEqualsException(String message) {
//			super(message);
//		}
//	}
//}
