/*
 * Class: SwingSample - represents sample/data point from Swing data table 
 *
 */



public class SwingSample {
    private int index, timeStamp;
    private double aX, aY, aZ, wX, wY, wZ;


    public SwingSample(){
        this.index = -1;
        this.timeStamp = 0;
        this.aX = 0;
        this.aY = 0;
        this.aZ = 0;
        this.wX = 0;
        this.wY = 0;
        this.wZ = 0;
    }
    public SwingSample(int index, int time, double ax, double ay, double az,
                       double wx, double wy, double wz){
        this.index = index;
        this.timeStamp = time;
        this.aX = ax;
        this.aY = ay;
        this.aZ = az;
        this.wX = wx;
        this.wY = wy;
        this.wZ = wz;
    }

    public int getTimestamp(){
        return this.timeStamp;
    }

    public int getIndex(){
        return this.index;
    }

    public double getXYZ(column data) throws IllegalArgumentException {
        if(data == column.TIMESTAMP){
            return this.timeStamp;
        } else if(data == column.AX){
            return this.aX;
        } else if (data == column.AY){
            return this.aY;
        } else if (data == column.AZ){
            return this.aZ;
        } else if (data == column.WX){
            return this.wX;
        } else if(data == column.WY){
            return this.wY;
        } else if(data == column.WX) {
            return this.wZ;
        } else {
            return 0;
        }

    }




}