/*
 * Copyright 2014 jamietech. All rights reserved.
 * https://github.com/jamietech/MinecraftServerPing
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package ch.jamiete.mcping;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * References:
 * http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
public class MinecraftPingReply {
    private final String description;
    private final Players players;
    private final Version version;
    private final String favicon;

    public MinecraftPingReply(JSONObject object) {
        description = object.get("description").toString();
        players = new Players((JSONObject) object.get("players"));
        version = new Version((JSONObject) object.get("version"));
        favicon = (String) object.get("favicon");
    }
    /**
     * @return the MOTD
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return @{link Players}
     */
    public Players getPlayers() {
        return this.players;
    }

    /**
     * @return @{link Version}
     */
    public Version getVersion() {
        return this.version;
    }

    /**
     * @return Base64 encoded favicon image
     */
    public String getFavicon() {
        return this.favicon;
    }

    public class Players {
        private final int max;
        private final int online;
        private final List<Player> sample;

        public Players(JSONObject object) {
            max = ((Number) object.get("max")).intValue();
            online = ((Number) object.get("online")).intValue();
            List<Player> players = new ArrayList<Player>();
            JSONArray sample = (JSONArray) object.get("sample");
            if (sample != null) {
                for (Object player : sample) {
                    if (player instanceof JSONObject) {
                        players.add(new Player((JSONObject) player));
                    }
                }
            }
            this.sample = Collections.unmodifiableList(players);
        }
        /**
         * @return Maximum player count
         */
        public int getMax() {
            return this.max;
        }

        /**
         * @return Online player count
         */
        public int getOnline() {
            return this.online;
        }

        /**
         * @return List of some players (if any) specified by server
         */
        public List<Player> getSample() {
            return this.sample;
        }
    }

    public class Player {
        private final String name;
        private final String id;

        public Player(JSONObject object) {
            name = (String) object.get("name");
            id = (String) object.get("id");
        }
        /**
         * @return Name of player
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return Unknown
         */
        public String getId() {
            return this.id;
        }

    }

    public class Version {
        private final String name;
        private final int protocol;

        public Version(JSONObject object) {
            name = (String) object.get("name");
            protocol = ((Number) object.get("protocol")).intValue();
        }
        /**
         * @return Version name (ex: 13w41a)
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return Protocol version
         */
        public int getProtocol() {
            return this.protocol;
        }
    }

}
