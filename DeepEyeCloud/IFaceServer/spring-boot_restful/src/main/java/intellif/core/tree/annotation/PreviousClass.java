package intellif.core.tree.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import intellif.core.tree.itf.TreeNode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PreviousClass {

    Class<? extends TreeNode> value();

}
