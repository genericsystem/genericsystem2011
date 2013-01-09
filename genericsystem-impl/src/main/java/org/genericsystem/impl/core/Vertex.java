//package org.genericsystem.impl.core;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.Set;
//
//import org.genericsystem.api.core.Engine;
//import org.genericsystem.api.core.Generic;
//import org.genericsystem.impl.core.Statics.Primaries;
//
///**
// * @author Nicolas Feybesse
// *
// */
//public class Vertex {
//
//	private final Supers supers;
//	private final Generic[] components;
//
//	public Vertex(Generic[] directSupers,Generic[] components ) {
//		this(new Supers(directSupers),components);
//	}
//
//	public Vertex(Supers supers,Generic[] components ) {
//		assert supers!=null;
//		assert supers.directSupers!=null;
//		assert components!=null;
//		assert supers.size() >= 1;
//		this.supers =supers;
//		this.components = components;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof Vertex))
//			return false;
//		return supers.equals(((Vertex) obj).supers) && Arrays.equals(components, (((Vertex) obj).components));
//	}
//
//	private static class Supers {
//		private final Generic[] directSupers;
//		private final Primaries primaries;
//
//		private Supers(Generic[] directSupers) {
//			assert directSupers!=null;
//			assert directSupers.length>0;
//			directSupers = cleanDirectSupers(directSupers);
//			this.directSupers = directSupers;
//			for (Generic g1 : directSupers)
//				for (Generic g2 : directSupers)
//					if(!g1.equals(g2)) {
//						assert !g1.inheritsFrom(g2) : ""+Arrays.toString(directSupers);
//					}
//			primaries= new Primaries(directSupers);		
//		}
//
//		private Generic[] cleanDirectSupers(final Generic[] dirtySupers){
//			Set<Generic> cleanGenerics = new LinkedHashSet<Generic>(){
//				private static final long serialVersionUID = 5136601964179739382L;
//
//				{
//					for(Generic dirtySuper : dirtySupers)
//						add(dirtySuper);
//				}
//
//				@Override
//				public boolean add(Generic candidate) {
//					for (Generic generic : this)
//						if (generic.inheritsFrom(candidate)) {
//							return false;
//						}
//					Iterator<Generic> it = this.iterator();
//					while (it.hasNext()) {
//						Generic next= it.next();
//						if (candidate.inheritsFrom(next)) {
//							it.remove();
//						}
//					}
//					return super.add(candidate);
//				}
//			};
//			return (Generic[]) cleanGenerics
//					.toArray(new Generic[cleanGenerics.size()]);
//
//		}
//
//		private int size(){
//			return directSupers.length;
//		}
//
//		private Generic get(int i){
//			return directSupers[i];
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if(!(obj instanceof Supers))
//				return false;
//			return primaries.equals(((Supers) obj).primaries);
//		}
//
//		@Override
//		public int hashCode() {
//			return primaries.hashCode();
//		}
//	}
//
//	@Override
//	public int hashCode() {
//		return supers.hashCode();
//	}
//
//	public boolean isSuperOf(Engine engine,Vertex vertex){
//		return new InheritanceVisitor(engine).isSuperOf(vertex);
//	}
//
//	@Override
//	public String toString() {
//		return (Arrays.toString(supers.directSupers)) + "/" + toString(components);
//	}
//
//	private String toString(Object[] a) {
//		if (a == null)
//			return "null";
//
//		int iMax = a.length - 1;
//		if (iMax == -1)
//			return "[]";
//
//		StringBuilder b = new StringBuilder();
//		b.append('[');
//		for (int i = 0;; i++) {
//			if (this.equals(a[i]))
//				b.append("this");
//			else
//				b.append(String.valueOf(a[i]));
//			if (i == iMax)
//				return b.append(']').toString();
//			b.append(", ");
//		}
//	}
//
//
//
//	private class InheritanceVisitor extends HashSet<Vertex> {
//
//		private static final long serialVersionUID = 3948213390765564875L;
//		private final Engine engine;
//
//		private InheritanceVisitor(Engine engine) {
//			this.engine = engine;
//		}
//
//		private boolean isSuperOf(Vertex vertex) {
//			//System.out.println("vertex : "+true+" "+Vertex.this +" <----- "+vertex);
//			if (!add(vertex))
//				return false;
//			if(Vertex.this.equals(vertex)) {
//				//if(Statics.isCurrentThreadDebugged() && Vertex.this.toString().contains("]/[Car, Person]") )
//					//System.out.println("vertex : "+true+" "+Vertex.this +" <----- "+vertex);
//				return true;			
//			}
//			boolean result = internalIsSuperOf(vertex);
//			//if(Statics.isCurrentThreadDebugged() && Vertex.this.toString().contains("]/[Car, Person]") )
//				//System.out.println("vertex : "+result+" "+Vertex.this +" <----- "+vertex);
//			return result;
//		}
//
//
//		protected boolean internalIsSuperOf(Vertex vertex) {
//			for (int i = 0; i < vertex.supers.size(); i++) {
//				Generic directSuper = vertex.supers.get(i);
//				if (directSuper.isEngine()) {
//					if (vertex.supers.size() > 1)
//						if(isSuperOf(new Vertex(Statics.truncate(i, vertex.supers.directSupers), vertex.components)))
//							return true;
//				}
//				else
//					for (Generic superGeneric : ((GenericImpl) directSuper).directSupers)
//						if(isSuperOf(new Vertex(Statics.replace(i, vertex.supers.directSupers, superGeneric), vertex.components)))
//							return true;
//			}
//			for (int i = 0; i < vertex.components.length; i++) {
//				Generic component = vertex.components[i];
//				if (component == null){
//					if(isSuperOf(new Vertex(vertex.supers, Statics.replace(i, vertex.components, engine))))
//						return true;
//				}
//				else if (component.isEngine()) {
//					if(isSuperOf(new Vertex(vertex.supers, Statics.truncate(i, vertex.components))))
//						return true;
//				}
//				else
//					for (Generic superGeneric : ((GenericImpl) component).directSupers)
//						if(isSuperOf(new Vertex(vertex.supers, Statics.replace(i, vertex.components, superGeneric))))
//							return true;
//			}
//			return false;
//		}
//		
//		
//	}
//	
//	private static class Wrapper {
//		private final Generic[] interfaces;
//		private final Generic[] components;
//
//		private Wrapper(Generic[] interfaces, Generic[] components) {
//			this.interfaces = interfaces;
//			this.components = components;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (!(obj instanceof Wrapper))
//				return false;
//			return Arrays.equals(interfaces, (((Wrapper) obj).interfaces)) && Arrays.equals(components, (((Wrapper) obj).components));
//		}
//
//		@Override
//		public int hashCode() {
//			return Arrays.hashCode(interfaces);
//		}
//	}
// }