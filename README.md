# Navigatior
This plugin allows you to add a Navigation-Tool to your Server.

## How does it work?
First of all you have to create a Map.
   ```
   /destination create <mapName>
   ```
If you are not satisfied with the mapname you can easily remove it.
   ```
   /destination remove <mapName>
   ```
Now you have to set some Destinations.
   ```
   /destination set <mapName> <destinationName>
   ```
And again if you are not satisfied with the name you can remove it.
   ```
   /destination remove <mapName> <destinationName>
   ```
If you ever forget the name of a destination.
   ```
   /destination list <mapName>
   ```
After you added your destination the most important step comes.
The addition of the checkpoints. Checkpoints are the points which connects
different destinations with each other. So to add checkpoints.
   ```
   /destination add <mapName>
   ```
After you entered this command you are in the CheckPointAddMode, this means if
you place a GoldBlock on the ground a new Checkpoint is created. If you remove
this block in the CheckPointAddMode the Checkpoint is removed.
If you want to leave the CheckPointAddMode enter:
   ```
   /destination add <mapName>
   ```
After reloading/restarting your Server
you can show the path to a given Destination with Particles:
   ```
   /destination find <mapName> <destinationName>
   ```
or Blocks
   ```
   /destination findblock <mapName> <destinationName>
   ```
or if you are lazy you can ride on a pig
   ```
   /destination ride <mapName> <destinationName>
   ```

My advice is that you should place enough Checkpoints because if 
the MaxDistanceBetweenPoints option is set to low the plugin might
not find a path to your desired destination.
