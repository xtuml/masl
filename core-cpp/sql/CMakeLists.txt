# ----------------------------------------------------------------------------
# (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# ----------------------------------------------------------------------------
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------
# Classification: UK OFFICIAL
# ----------------------------------------------------------------------------

find_package(Boost CONFIG REQUIRED)

simple_add_shared_library (
  NAME      sql
  SOURCES    
            CacheStrategy.cc
            ResourceMonitor.cc
            Criteria.cc
            Schema.cc
            AssignerStateMapper.cc
            WriteOnChangeEnabler.cc
            DatabaseUnitOfWork.cc
            DatabaseFactory.cc
            AssignerStateFactory.cc
            CriteriaFactory.cc
            TimerMapper.cc
            TimerMapperSqlFactory.cc
            TimerMapperUnitOfWork.cc
            ObjectSqlRepository.cc
            RelationshipSqlRepository.cc
  LINKS
            swa-headers
            Boost::boost
  EXPORT    MaslCore
  INCLUDES
            sql/AssignerStateImpl.hh
            sql/AssignerStateMapper.hh
            sql/CacheStrategy.hh
            sql/Criteria.hh
            sql/Database.hh
            sql/DatabaseFactory.hh
            sql/DatabaseUnitOfWork.hh
            sql/Exception.hh
            sql/Iterator.hh
            sql/ObjectMapper.hh
            sql/ObjectMapperItr.hh
            sql/ObjectMapperUnitOfWork.hh
            sql/ObjectSql.hh
            sql/ObjectSqlGenerator.hh
            sql/ObjectSqlRepository.hh
            sql/Population.hh
            sql/RelationshipAtomicity.hh
            sql/RelationshipAssociativeContainers.hh
            sql/RelationshipBinaryDefinitions.hh
            sql/RelationshipBinaryMapper.hh
            sql/RelationshipBinarySqlGenerator.hh
            sql/RelationshipContainers.hh
            sql/RelationshipMapperUnitOfWork.hh
            sql/RelationshipSql.hh
            sql/RelationshipSqlGenerator.hh
            sql/RelationshipSqlRepository.hh
            sql/RelationshipTenaryDefinitions.hh
            sql/RelationshipTenaryMapper.hh
            sql/RelationshipTenaryMapperUnitOfWork.hh
            sql/RelationshipTenarySqlGenerator.hh
            sql/ResourceMonitor.hh
            sql/ResourceMonitorObserver.hh
            sql/Schema.hh
            sql/StatementFormatter.hh
            sql/UnitOfWorkObserver.hh
            sql/Util.hh
            sql/WriteOnChangeEnabler.hh
  )

