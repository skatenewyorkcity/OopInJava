package module6;

import java.util.ArrayList;
//**

import java.util.*;

//**

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
//import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	//**
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	private HashMap<Integer, AirportMarker> airportsRoutes;
	
	//**
	
	public void setup() {
		// setting up PAppler
		size(900,700, OPENGL);
		
		// setting up map and default events
		//map = new UnfoldingMap(this, 200, 50, 650, 600);
		
		//**
		
		map = new UnfoldingMap(this, 200, 50, 650, 600, new OpenStreetMap.OpenStreetMapProvider());
		
		//**
		
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		//**
		
		airportsRoutes = new HashMap<Integer, AirportMarker>();
		
		//**
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
	
			//**
			
			airportsRoutes.put(Integer.parseInt(feature.getId()), m);
			
			//**
			
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//**
			
			sl.setHidden(true);
			
			//**
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
			
			//**
			
			if(airportsRoutes.containsKey(source) && airportsRoutes.containsKey(dest)){
				//System.out.println(airportRoutes.get(dest).getLocation());
				airportsRoutes.get(source).addRoute(sl);
				airportsRoutes.get(dest).addRoute(sl);
			}
			
			//**
			
		}
		
		//**
		
		sortAndPrint(20);
			
		//**
				
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		//**
		
		background(0);
		
		//**
		
		//background(0);
		map.draw();
		
		//**
		
		addKey();
		
		//**
		
	}
	
	//**
	
	//Event handler that gets called automatically when mouse moves.
	@Override
	public void mouseMoved()
	{
		//clear the last selection
		if(lastSelected != null){
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerIfHover(airportList);
	}
	
	private void selectMarkerIfHover(List<Marker> markers)
	{
		//Abort if there's already a marker
		if(lastSelected != null){
			return;
		}
		
		for(Marker m : markers)
		{
			CommonMarker marker = (CommonMarker)m;
			if(marker.isInside(map, mouseX, mouseY)){
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	@Override
	public void mouseClicked()
	{
			if(lastClicked != null){
				setInitialHiddenMarkers();
				lastClicked = null;
			}
			else if(lastClicked == null)
			{
				checkMarkersForClick();
			}
	}

	private void setInitialHiddenMarkers(){
		for(Marker marker : airportList){
			marker.setHidden(false);
		}
		
		for(Marker marker : routeList){
			marker.setHidden(true);
		}
	}
	
	private void checkMarkersForClick(){
		if(lastClicked != null) return;
		//List<Marker> nothiddenAirportMarker = new ArrayList<Marker>();
		for(Marker marker : airportList){
			if(!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				for(Marker s1 : ((AirportMarker)lastClicked).routes){
					s1.setHidden(false);
					//int source = Integer.parseInt((String)sl.getProperty("source"));
					//int dest = Integer.parseInt((String)sl.getProperty("destination"));
					//notHiddenAirportMarker.add(airportsRoutes.get(source));
					//notHiddenAirportMarker.add(airportsRoutes.get(dest));
				}
				for(Marker mk: airportList){
					if(mk != lastClicked){
						mk.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	private void sortAndPrint(int numToPrint){
		List<AirportMarker> al = new ArrayList<AirportMarker>();
		for(Marker m : airportList){
			AirportMarker am = (AirportMarker)m;
			al.add(am);
		}
		Collections.sort(al);
		int actualNumToPrint = (numToPrint >= al.size() ? al.size() : numToPrint);
		for(int index = 0; index < actualNumToPrint; index++){
			System.out.println(al.get(index));
		}
	}
	
	private void addKey(){
		//Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		stroke(0, 15, 15);
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Airport Route Key", xbase+25, ybase+25);
				
		fill(255, 255, 0);
		stroke(0, 15, 15);
		ellipse(xbase+10, ybase+52, 12, 12);
		
		fill(0, 0, 255);
		stroke(0, 15, 15);
		ellipse(xbase+10, ybase+82, 12, 12);	
		
		fill(255, 0, 0);
		stroke(0, 15, 15);
		ellipse(xbase+10, ybase+112, 12, 12);
		
		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		textSize(11);
		text("30+ Routes", xbase+23, ybase+50);
		text("30 to 10 Routes", xbase+23, ybase+80);
		text("Less than 10 Routes", xbase+23, ybase+110);

	}
	
	//**
}
