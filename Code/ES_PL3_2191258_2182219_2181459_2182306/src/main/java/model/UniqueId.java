package model;

public abstract class UniqueId {
    private int id = Integer.MIN_VALUE;

    UniqueId() {
    }

    //id is not validated at this stage because it can be invalid for internal uses
    UniqueId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    //This method is to be called after creating the model so there is no sense in have a invalid id
    public void setId(int id) {
        if (id < 1)
            throw new IllegalArgumentException("Invalid id: " + id);

        if (this.id != Integer.MIN_VALUE)
            throw new IllegalStateException("This object already have an id: " + this.id);

        this.id = id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UniqueId uniqueId && uniqueId.id == id;
    }

    @Override
    public String toString() {
        return "UniqueId: " + id;
    }
}
