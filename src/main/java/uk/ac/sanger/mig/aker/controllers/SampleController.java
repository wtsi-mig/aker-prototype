package uk.ac.sanger.mig.aker.controllers;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.GroupRequest;
import uk.ac.sanger.mig.aker.domain.Alias;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.seeders.SampleSeeder;
import uk.ac.sanger.mig.aker.services.GroupService;
import uk.ac.sanger.mig.aker.services.SampleService;
import uk.ac.sanger.mig.aker.services.TypeService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/samples")
public class SampleController extends BaseController {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private AliasRepository aliasRepository;

	@Resource
	private SampleService sampleService;

	@Resource
	private TypeService typeService;

	@Resource
	private GroupService groupService;

	@Resource
	private GroupRepository groupRepository;

	@Autowired
	private SampleSeeder seeder;

	@PostConstruct
	private void init() {
		setTemplatePath("samples");
	}

	@RequestMapping("/seed")
	@ResponseBody
	public String seed() {
		seeder.seed();
		return "Done";
	}

	@RequestMapping("/seedSamples")
	@ResponseBody
	public String seedSamples() {
		seeder.seedSamples();
		return "Done";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("groupRequest", new GroupRequest());

		return view(Action.INDEX);
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<Sample> json(Pageable p) {
		return sampleService.findAll(p);
	}

	@RequestMapping("/show/{barcode}")
	public String show(@PathVariable("barcode") String barcode, Model model) {
		final Optional<Sample> sample = sampleService.findByBarcode(barcode);
		if (sample.isPresent()) {
			model.addAttribute("sample", sample.get());
		} else {
			return "redirect:/samples/?404";
		}

		model.addAttribute("alias", new Alias());

		return view(Action.SHOW);
	}

	@RequestMapping("/create")
	public String create(Model model) {
		model.addAttribute("types", typeService.findAll());
		model.addAttribute("sampleRequest", new SampleRequest());

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public String store(@Valid @ModelAttribute SampleRequest request, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("types", typeService.findAll());
			return view(Action.CREATE);
		}

		sampleService.createSamples(request);
		return "redirect:/samples/";
	}

	@RequestMapping(value = "/update/{barcode}/add-alias", method = RequestMethod.PUT)
	public String addAlias(@PathVariable("barcode") String barcode, @Valid @ModelAttribute Alias alias, BindingResult bindingResult) {
		final Optional<Sample> optSample = sampleService.findByBarcode(barcode);

		if (optSample.isPresent()) {
			final Sample sample = optSample.get();

			if (!bindingResult.hasErrors()) {
				alias.setSample(sample);
				aliasRepository.save(alias);
			}

			return "redirect:/samples/show/" + sample.getBarcode();
		}

		return "redirect:/samples/?404" + optSample.toString();
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, BindingResult binding, Model model) {
		if (!binding.hasErrors()) {
			groupService.createGroup(groupRequest);

			return "redirect:/";
		}

		return view("group");
	}

	@RequestMapping(value = "/byGroup/{groupId}/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<Sample> showJson(@PathVariable long groupId, Pageable pageable, Model model) {
		model.addAttribute("group", groupRepository.findOne(groupId));

		return sampleRepository.byGroupId(groupId, pageable);
	}

}
