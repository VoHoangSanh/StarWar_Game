package game.component;

public class Key {
    private boolean key_up;       // W
    private boolean key_down;     // S
    private boolean key_left;     // A
    private boolean key_right;    // D
    private boolean key_shoot;    // Left mouse

    public boolean isKey_up() {
        return key_up;
    }

    public void setKey_up(boolean key_up) {
        this.key_up = key_up;
    }

    public boolean isKey_down() {
        return key_down;
    }

    public void setKey_down(boolean key_down) {
        this.key_down = key_down;
    }

    public boolean isKey_left() {
        return key_left;
    }

    public void setKey_left(boolean key_left) {
        this.key_left = key_left;
    }

    public boolean isKey_right() {
        return key_right;
    }

    public void setKey_right(boolean key_right) {
        this.key_right = key_right;
    }

    public boolean isKey_shoot() {
        return key_shoot;
    }

    public void setKey_shoot(boolean key_shoot) {
        this.key_shoot = key_shoot;
    }
    
    
     private boolean key_bigShoot;  // Right mouse

    // ... (các getter/setter khác giữ nguyên)

    public boolean isKey_bigShoot() {
        return key_bigShoot;
    }

    public void setKey_bigShoot(boolean key_bigShoot) {
        this.key_bigShoot = key_bigShoot;
    }
}