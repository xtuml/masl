import conan
from pathlib import Path
import shutil
import io

class ConanFile(conan.ConanFile):
    name = "masl_examples_petrol"
    version = "1.0"
    user = "xtuml"

    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'
