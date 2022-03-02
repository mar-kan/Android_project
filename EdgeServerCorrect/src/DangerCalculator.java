public class DangerCalculator {
    boolean [] danger = new boolean[4];
    public double [] values;


    public DangerCalculator(double[] values)
    {
        this.values = values;
        danger[0]=false;
        danger[1]=false;
        danger[2]=false;
        danger[3]=false;

    }

    /** creates a danger message to be sent **/
    public String createDangerMessage()
    {
        String dangerString="No Danger";

        if(danger[0] && danger[1] && !danger[2] && !danger[3])
        {
            dangerString="High Danger";
            return dangerString;
        }
        if(!danger[0] && !danger[1] && danger[2] && danger[3])
        {
            dangerString="Medium Danger";
            return dangerString;
        }
        if(danger[0] && !danger[1] && !danger[2] && !danger[3])
        {
            dangerString="High Danger";
            return dangerString;
        }
        if(danger[0] && danger[1] && danger[2] && danger[3])
        {
            dangerString="High Danger";
            return dangerString;
        }

        return dangerString;
    }

    /** checks for every sensor if the threshold is surpassed **/
    public void calculateDanger() throws Exception
    {
        if (values[0] < 0.0 || values[0] > 0.25)
        {
            throw new Exception("Error. Invalid smoke sensor value.");
        }
        else
        {
            if (values[0] > 0.14)
                danger[0] = true;
        }

        if (values[1] < 0.0 || values[1] > 11)
        {
            throw new Exception("Error. Invalid smoke sensor value.");
        }
        else
        {
            if (values[1] > 1.0065)
                danger[1] = true;
        }

        if (values[2] < -5 || values[2] > 80)
        {
            throw new Exception("Error. Invalid smoke sensor value.");
        }
        else
        {
            if (values[2] > 50)
                danger[2] = true;
        }

        if (values[3] < 0 || values[3] > 11)
        {
            throw new Exception("Error. Invalid smoke sensor value.");
        }
        else
        {
            if (values[3] > 6)
                danger[3] = true;
        }
    }
}
