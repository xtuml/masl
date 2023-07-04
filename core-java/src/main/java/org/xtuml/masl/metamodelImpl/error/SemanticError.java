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
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.error.MaslError;
import org.xtuml.masl.metamodelImpl.common.Position;

import java.text.MessageFormat;

public class SemanticError extends MaslError {

    public SemanticError(final SemanticErrorCode code, final Position position, final Object... args) {
        super(code);
        format = new MessageFormat(code.getMessageFormat());
        this.position = position;
        this.args = args;
    }

    @Override
    public String getMessage() {

        final String posString = position == null ? "<unknown position>" : position.getText();
        final String message = format.format(args);
        final String context = position != null ? "\n" + position.getContext() : "";

        return posString + ": " + getErrorCode().getErrorType() + ": " + message + context;

    }

    private final Position position;

    private final MessageFormat format;

    private final Object[] args;

}
