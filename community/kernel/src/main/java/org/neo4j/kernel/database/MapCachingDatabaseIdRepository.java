/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.configuration.helpers.NormalizedDatabaseName;

public class MapCachingDatabaseIdRepository implements DatabaseIdRepository.Caching
{
    private final DatabaseIdRepository delegate;
    private final Map<String,DatabaseId> databaseIds;

    public MapCachingDatabaseIdRepository( DatabaseIdRepository delegate )
    {
        this.delegate = delegate;
        this.databaseIds = new ConcurrentHashMap<>();
    }

    @Override
    public DatabaseId get( NormalizedDatabaseName databaseName )
    {
        if ( SYSTEM_DATABASE_ID.name().equals( databaseName.name() ) )
        {
            return SYSTEM_DATABASE_ID;
        }
        return databaseIds.computeIfAbsent( databaseName.name(), delegate::get );
    }

    @Override
    public void invalidate( DatabaseId databaseId )
    {
        databaseIds.remove( databaseId.name() );
    }
}