package scalc.internal.expr.multiline;

import java.util.ArrayList;
import java.util.List;

public class AssignExpression extends LineExpression {
    private String name;
    private List<String> params = new ArrayList<String>();

    public AssignExpression(String rawExpression) {
        super(rawExpression);
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }
}
