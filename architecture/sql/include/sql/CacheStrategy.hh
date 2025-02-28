/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_CacheStrategy_HH
#define Sql_CacheStrategy_HH

#include <map>
#include <memory>
#include <stdint.h>

namespace SQL {

    // *************************************************
    // *************************************************
    class CacheStrategy {
      public:
        virtual ~CacheStrategy() {}

        virtual std::string getName() const = 0;
        virtual uint32_t getOperationalCount(const uint32_t population) const = 0;

        virtual bool allowLinearFind(const uint32_t population) const = 0;
        virtual bool allowFullCaching(const uint32_t population) const = 0;

        virtual CacheStrategy *clone() const = 0;

      protected:
        CacheStrategy() {}
    };

    // *************************************************
    // *************************************************
    class CacheStrategyFactory {
      public:
        static CacheStrategyFactory &singleton();

        bool registerStrategy(
            const std::string &domain, const std::string &object, const std::shared_ptr<CacheStrategy> CacheStrategy
        );

        std::shared_ptr<CacheStrategy> getStrategy(const std::string &domain, const std::string &object);

      private:
        CacheStrategyFactory();
        ~CacheStrategyFactory();

        CacheStrategyFactory(const CacheStrategyFactory &rhs);
        CacheStrategyFactory &operator=(const CacheStrategyFactory &rhs);

      private:
        typedef std::map<std::string, std::shared_ptr<CacheStrategy>> DefinedStratergyDbType;

        DefinedStratergyDbType definedStratergyDb_;

        bool disabled_;
    };

} // namespace SQL
#endif
