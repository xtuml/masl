import conan
import git

class ConanFile(conan.ConanFile):
    name = "calculator-masl"
    version = ver[1:] if (ver := git.Repo(search_parent_directories=True).git.describe('--tags')).startswith('v') else ver
    user = "xtuml"
    channel = "stable"
    python_requires = 'masl_conan/[>=4.2.2]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    def requirements(self):
        super().requirements()
        self.requires("masl_utils/4.2.2@xtuml/stable")
