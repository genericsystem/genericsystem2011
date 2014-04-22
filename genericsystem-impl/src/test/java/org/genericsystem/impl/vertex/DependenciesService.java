package org.genericsystem.impl.vertex;

import java.util.Set;

public interface DependenciesService {

	Set<Vertex> getInstances();

	Set<Vertex> getInheritings();

	Set<Vertex> getComposites();

	boolean isPlugged() throws NotFoundException;

	Vertex getPlugged(boolean throwNotFoundException) throws NotFoundException;

	Vertex plug(boolean throwExistException);

}
