# Navigator
This plugin allows you to add a Navigation-Tool to your Server.

## Usage
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
If you havent enabled the auto_reload option in the config.
You have to issue the following command(you can reload every map by simply removing the mapName argument) 
```
/destination reload [mapName]
```
If you want to check if every destination is close enough to a checkpoint, you can show all checkpoints and destinations
as blocks
 ```
 /destination show <mapName>
 ```
 All GoldBlocks are Checkpoints and all DiamondBlocks are Destinations.
You can show the path to a given Destination with Particles:
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
## How does it work?
You are wondering how my Plugin works.
It is pretty easy.
I simply convert the locations into vertices.
Then I build an Adjacencymatrix of the vertices by collecting all vertices in a certain range of another vertex.
Out of the Adjacencymatrix I can built a list of edges.
Then I use the Dijkstra Algorithm to find the shortest path from a source vertex to every other vertex.
And thats it.
## What is the runtime?
The runtime is pretty low. For 36 Checkpoints and 6 Destinations it takes about 0.5 - 1 ms.
## Known Bugs
There is just one "bug" that I know. Its that if the player moves way the fast(flying) the system gets a bit confused on which checkpoints have to be removed.
