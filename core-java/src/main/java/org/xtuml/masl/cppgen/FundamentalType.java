/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

/**
 * Provides a series of static variables representing the C++ fundamental types.
 */
public final class FundamentalType extends Type {

    /**
     * Represents C++ void type
     */
    public final static FundamentalType VOID = new FundamentalType("void");

    /**
     * Represents C++ bool type
     */
    public final static FundamentalType BOOL = new FundamentalType("bool");

    /**
     * Represents C++ char type
     */
    public final static FundamentalType CHAR = new FundamentalType("char");

    /**
     * Represents C++ wchar_t type
     */
    public final static FundamentalType WCHAR = new FundamentalType("wchar_t");

    /**
     * Represents C++ signed char type
     */
    public final static FundamentalType SCHAR = new FundamentalType("signed char");

    /**
     * Represents C++ short int type
     */
    public final static FundamentalType SHORT = new FundamentalType("short");

    /**
     * Represents C++ int type
     */
    public final static FundamentalType INT = new FundamentalType("int");

    /**
     * Represents C++ long type
     */
    public final static FundamentalType LONG = new FundamentalType("long");

    /**
     * Represents C++ long long type. This is not a standard C++ type, but is
     * often provided as an extension.
     */
    public final static FundamentalType LONGLONG = new FundamentalType("long long");

    /**
     * Represents C++ unsigned char type
     */
    public final static FundamentalType UCHAR = new FundamentalType("unsigned char");

    /**
     * Represents C++ unsigned short type
     */
    public final static FundamentalType USHORT = new FundamentalType("unsigned short");

    /**
     * Represents C++ unsigned int type
     */
    public final static FundamentalType UINT = new FundamentalType("unsigned int");

    /**
     * Represents C++ unsigned long type
     */
    public final static FundamentalType ULONG = new FundamentalType("unsigned long");

    /**
     * Represents C++ unsigned long long type. This is not a standard C++ type,
     * but is often provided as an extension.
     */
    public final static FundamentalType ULONGLONG = new FundamentalType("unsigned long long");

    /**
     * Represents C++ float type
     */
    public final static FundamentalType FLOAT = new FundamentalType("float");

    /**
     * Represents C++ double type
     */
    public final static FundamentalType DOUBLE = new FundamentalType("double");

    /**
     * Represents C++ long double type
     */
    public final static FundamentalType LONGDOUBLE = new FundamentalType("long double");

    /**
     * Represents C++ size_t type
     */
    public final static FundamentalType SIZE_T = new FundamentalType("size_t");

    /**
     * Constructs a FundamentalType with the given name
     * <p>
     * <p>
     * The name of the type
     */
    private FundamentalType(final String name) {
        super(name);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * It is never more efficient to pass any funcdabamental types by reference, so
     * always returns false.
     *
     * @return <code>false</code>
     *
     */
    boolean preferPassByReference() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }
}
