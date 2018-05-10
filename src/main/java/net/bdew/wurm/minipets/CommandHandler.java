package net.bdew.wurm.minipets;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.Zones;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandHandler {
    public static MessagePolicy handleCommands(Communicator communicator, String message) {
        try {
            if (communicator.getPlayer().getPower() == 5 && message.equals("#reloadpets")) {
                PetTypes.load(MiniPets.typesFile);
                communicator.sendNormalServerMessage("Reloaded pet types, going to refresh existing creatures...");

                for (Creature c : Creatures.getInstance().getCreatures()) {
                    if (c.getTemplate() == PetCreature.template) {
                        MiniPetAIData data = MiniPetAIData.get(c);
                        if (data.getType() != null && PetTypes.exists(data.getType().id)) {
                            c.setVisible(false);
                            data.setType(PetTypes.get(data.getType().id));
                            c.setVisible(true);
                        } else {
                            communicator.sendNormalServerMessage(String.format("Pet #%d (%s) has invalid type, deleting", c.getWurmId(), c.getName()));
                            c.destroy();
                        }
                    }
                }

                communicator.sendNormalServerMessage("... and items ...");

                for (Item i : Items.getAllItems()) {
                    if (i.getTemplateId() == PetItems.petDecorativeId) {
                        Item parent = i.getParentOrNull();
                        if (parent == null || parent.getTemplate().getName().equals("item hook")) {
                            Item toRefresh = i.getTopParentOrNull();
                            if (toRefresh == null) toRefresh = i;
                            Zone zone = Zones.getZone(toRefresh.getTilePos(), toRefresh.isOnSurface());
                            if (zone != null) {
                                zone.removeItem(toRefresh);
                                zone.addItem(toRefresh);
                            }
                        }
                    }
                }

                communicator.sendNormalServerMessage("... all done!");
                return MessagePolicy.DISCARD;
            } else if (communicator.getPlayer().getPower() >= 1 && message.startsWith("#givepet ")) {
                StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                Utils.createPet(communicator.getPlayer(), Integer.parseInt(tokens.nextToken()), null);
                return MessagePolicy.DISCARD;
            } else if (communicator.getPlayer().getPower() == 5 && message.startsWith("#dropeggs ")) {
                try {
                    StringTokenizer tokens = new StringTokenizer(message);
                    tokens.nextToken();
                    int rarity = Integer.parseInt(tokens.nextToken(), 10);
                    int amount = Integer.parseInt(tokens.nextToken(), 10);
                    communicator.sendNormalServerMessage(String.format("Spawning %d eggs with rarity %d", amount, rarity));
                    for (int eggNum = 0; eggNum < amount; eggNum++) {
                        while (true) {
                            int tileX = Server.rand.nextInt(Zones.worldTileSizeX);
                            int tileY = Server.rand.nextInt(Zones.worldTileSizeY);
                            int tile = Server.surfaceMesh.getTile(tileX, tileY);
                            byte type = Tiles.decodeType(tile);
                            if (Tiles.decodeHeight(tile) < 0 || Tiles.getTile(type).isRoad()) continue;
                            VolaTile vt = Zones.getOrCreateTile(tileX, tileY, true);
                            if (vt.getVillage() != null || vt.getStructure() != null || vt.getItems().length > 0)
                                continue;
                            Item egg = ItemFactory.createItem(PetItems.petEggId, 99f, (byte) rarity, null);
                            vt.addItem(egg, false, false);
                            MiniPets.logInfo(String.format("Spawned egg at %d,%d", tileX, tileY));
                            break;
                        }
                    }
                    communicator.sendNormalServerMessage("All done");
                } catch (NoSuchElementException e) {
                    communicator.sendNormalServerMessage("Usage: #dropeggs <rarity> <number>");
                } catch (Exception e) {
                    communicator.sendAlertServerMessage("Error: " + e.toString());
                }
                return MessagePolicy.DISCARD;
            } else return MessagePolicy.PASS;
        } catch (Exception e) {
            if (communicator.getPlayer().getPower() > 0) {
                communicator.sendAlertServerMessage("Error: " + e.toString());
            }
            MiniPets.logException("Error in player message", e);
            return MessagePolicy.DISCARD;
        }
    }
}
