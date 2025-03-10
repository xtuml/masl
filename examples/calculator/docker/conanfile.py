import os
import traceback

import conan
from pathlib import Path
from conan.tools.layout import basic_layout
from conan.tools.files import copy
import shutil
import io
import subprocess

class ConanFile(conan.ConanFile):

    name = "petrol_image"
    version = "1.0"
    user = "xtuml"

    python_requires = 'xtuml_deployer/[>=1.0 <2]@xtuml'

    def layout(self):
        basic_layout(self)

    def requirements(self):
        self.requires("masl_examples_calculator/[>=1.0]@xtuml")

    def deploy(self):
        deployer = self.python_requires['xtuml_deployer'].module.Deployer(self,'apps/calculator')

        deployer.executable('calculator_transient')
        deployer.library('Inspector')
        deployer.library('calculator_inspector')
        deployer.resource('schedule/*',packages=['masl_examples_calculator'], dest='schedule')
        deployer.resource('config/*',packages=['masl_examples_calculator'], dest='config')
        deployer.deploy()