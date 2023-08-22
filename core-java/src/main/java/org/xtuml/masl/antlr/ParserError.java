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
package org.xtuml.masl.antlr;

import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.error.ErrorType;
import org.xtuml.masl.error.MaslError;

public class ParserError extends MaslError {

    enum ParserErrorCode implements ErrorCode {
        parseError;

        @Override
        public ErrorType getErrorType() {
            return ErrorType.Error;
        }

    }

    public ParserError(final String position, final String message, final String context) {
        super(ParserErrorCode.parseError);
        this.position = position;
        this.message = message;
        this.context = context;
    }

    @Override
    public String getMessage() {
        return position + ": " + getErrorCode().getErrorType() + ": " + message + "\n" + context;
    }

    private final String position;
    private final String message;
    private final String context;

}
