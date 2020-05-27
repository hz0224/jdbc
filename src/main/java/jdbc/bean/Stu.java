package jdbc.bean;

public class Stu {
    private String id;  //学号
    private String name; //姓名
    private String age; //年龄
    private boolean is_deleted; //是否删除

    public Stu(String id, String name, String age, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.is_deleted = is_deleted;
    }

    public Stu() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", is_deleted=" + is_deleted +
                '}';
    }
}
