package com.chen.zookeeper.lock;

/**
 * @author ChenTian
 * @date 2020/7/10
 */
public class LockNode {
    private final String lockId;
    private final String nodePath;
    private boolean active;

    public LockNode(String lockId, String nodePath) {
        this.lockId = lockId;
        this.nodePath = nodePath;
    }

    public String getLockId() {
        return lockId;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
