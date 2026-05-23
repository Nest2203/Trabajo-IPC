package model; 

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class SessionActivity {
    
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>();
    private final DoubleProperty distance = new SimpleDoubleProperty(); 
    private final LongProperty duration = new SimpleLongProperty();     
    private final DoubleProperty totalAscent = new SimpleDoubleProperty(); 

    public SessionActivity(String name, LocalDateTime date, double distance, long duration, double totalAscent) {
        this.name.set(name);
        this.date.set(date);
        this.distance.set(distance);
        this.duration.set(duration);
        this.totalAscent.set(totalAscent);
    }

    public String getName() { 
        return name.get(); 
    }
    public void setName(String value) { 
        name.set(value); 
    }
    public StringProperty nameProperty() { 
        return name; 
    }

    public double getDistance() { 
        return distance.get(); 
    }
    public void setDistance(double value) { 
        distance.set(value); 
    }
    public DoubleProperty distanceProperty() {
        return distance; 
    }

    public long getDuration() { 
        return duration.get(); 
    }
    public void setDuration(long value) { 
        duration.set(value); 
    }
    public LongProperty durationProperty() { 
        return duration; 
    }
}
