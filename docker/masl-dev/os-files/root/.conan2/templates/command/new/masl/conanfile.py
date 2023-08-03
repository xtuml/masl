import conan

class ConanFile(conan.ConanFile):
    name = "{{name}}-masl"
    version = "{{version if version is defined else '0.1'}}"
    user = "{{user if user is defined else 'xtuml'}}"
    channel = "{{channel if channel is defined else 'stable'}}"
    python_requires = 'masl_conan/[>=0.1]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    exports_sources= "src/*"

    def requirements(self):
        self.requires("masl_core/[>=0.1]@xtuml/stable")
        {% if requires is defined -%}
            {% for require in requires -%}
        self.requires("{{ require }}")
            {% endfor %}
        {%- endif %}

    def build_requirements(self):
        self.tool_requires("masl_codegen/[>=0.1]@xtuml/stable")
            {% if tool_requires is defined -%}
                {% for require in tool_requires -%}
            self.tool_requires("{{ require }}")
                {% endfor %}
            {%- endif %}
