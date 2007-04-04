package org.deegree.security.owsproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.security.drm.model.User;

public class UsersOperationParameter extends OperationParameter {

    public static class OpParameterRoleEntry {
        public String[] roles;

        public OperationParameter op;

        public OpParameterRoleEntry(String[] roles, OperationParameter op) {
            this.roles = roles;
            this.op = op;
        }
    };

    static Map<Object, User> currentUserMap = new HashMap<Object, User>();

    List<OpParameterRoleEntry> opList;

    // XXX used ??
    OperationParameter op;

    // XXX need to deal with roles
    public static void setCurrentUser(User user) {
        currentUserMap.put(Thread.currentThread(), user);
    }

    public UsersOperationParameter(String name, String[] values,
            boolean userCoupled) {
        super(name, values, userCoupled);
        // TODO Auto-generated constructor stub
    }

    public UsersOperationParameter(String name, boolean any) {
        super(name, any);
    }

    // TODO: constructor with multiple values
    public UsersOperationParameter(String name,
                        List<OpParameterRoleEntry> opList, boolean userCoupled) {
        super(name, new String[] {}, false);

        // this.opRoleMap = opRoleMap;
        this.opList = opList;
    }

    private OperationParameter getOp() {

        User user = currentUserMap.get(Thread.currentThread());

        for (OpParameterRoleEntry opEntry : opList) {
            for (String role : opEntry.roles) {
                if (user.servletRequest.isUserInRole(role)
                        || role.equals("$default$"))
                    return opEntry.op;
            }
        }

        // XXX default policy: accept
        return new OperationParameter("dummy", true);
    }

    /**
     * 
     * @return
     */
    public List<String> getValues() {
        return getOp().getValues();
    }

    /**
     * returns the first value of the list as integer. This is useful for
     * operation parameter that only allow one single string expression (e.g.
     * BBOX)
     * 
     * @return
     */
    public String getFirstAsString() {
        return getOp().getFirstAsString();
    }

    /**
     * returns the first value of the list as integer. This is useful for
     * operation parameter that only allow one single integer expression (e.g.
     * maxHeight)
     * 
     * @return
     */
    public int getFirstAsInt() {
        return getOp().getFirstAsInt();
    }

    /**
     * returns the first value of the list as integer. This is useful for
     * operation parameter that only allow one single double expression (e.g.
     * resolution)
     * 
     * @return
     */
    public double getFirstAsDouble() {
        return getOp().getFirstAsDouble();
    }

    /**
     * @return Returns the userCoupled.
     */
    public boolean isUserCoupled() {
        return getOp().isUserCoupled();
    }

    /**
     * @return Returns the all.
     */
    public boolean isAny() {

        System.out.println("indirection");
        System.out.println(Thread.currentThread());

        User user = currentUserMap.get(Thread.currentThread());
        System.out.println("User is " + user.getName());

        return getOp().isAny();
    }
}
