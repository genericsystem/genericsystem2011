package org.genericsystem.jsf.example.component;

import java.util.List;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Type;

public class SelectionComponent extends AbstractComponent {
    
    protected AbstractComponent child;

    public SelectionComponent() {
        this(null);
    }
    
    public SelectionComponent(AbstractComponent parent) {
        super(parent);
        this.children = initChildren();
    }

    @Override
    @SuppressWarnings("empty-statement")
    public List<? extends AbstractComponent> initChildren() {
        return getCache().getAllTypes().project(new Projector<AbstractComponent,Type>(){
            @Override
            public AbstractComponent project(Type element) {
                return new TypeComponent(SelectionComponent.this, element);
            }
        });
    }

    @Override
    public String getXhtmlPath() {
        return "/pages/selectionComponent.xhtml";
    } 
    
    public AbstractComponent getChild() {
            return child;
    }

    public void setChild(AbstractComponent child) {
        this.child = child;
    }

}
