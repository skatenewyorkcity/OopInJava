package module5;

//**

import java.util.List;

//**

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CityMarker extends /*SimplePointMarker*/ CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void /*draw*/ drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 30, 30);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		
		// TODO: Implement this method
		
		//**
		
		String city = getCity();
		String country = getCountry();
		float population = getPopulation();
		String text = city + " " + country + " " + String.valueOf(population);
		pg.textSize(12);
		pg.fill(51, 51, 255);
		pg.text(text, x+5, y);
		
		//**
	}
	
	
	
	/* Local getters for some city properties.  
	 */
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}

	//**
	
	@Override
	public void showThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers){
		//TODO Auto-generated method stub
		for(Marker cityMarker : cityMarkers){
			if(!this.equals(cityMarker)){
				cityMarker.setHidden(true);
			}
		}
		
		//Hiding the earthquakes which don't effect this cityMarker
		//City not threatened by an earthquake
		//if distance between eathquakeMarker and CityMarker > threatCircle()		 
		for(Marker earthquakeMarker : quakeMarkers){
			double threat = ((EarthquakeMarker) earthquakeMarker).threatCircle();
			if (earthquakeMarker.getDistanceTo(this.getLocation()) > threat){
				earthquakeMarker.setHidden(true);
			}
		}
	}
	
	//**
}

